import { Request, Response, NextFunction } from 'express';
import { authService } from '../services/authService';
import { User } from '../types';

export const authMiddleware = (req: Request, res: Response, next: NextFunction) => {
  const adminAuth = authService.getAdminBasicAuth();

  // Try Bearer token first
  const authHeader = req.headers.authorization;
  if (authHeader && authHeader.startsWith('Bearer ')) {
    const token = authHeader.substring(7, authHeader.length);
    const user = authService.verifyToken(token); // user now includes id and role
    if (user) {
      req.user = user;
      return next();
    }
  }

  // If Bearer token fails or is not present, try Basic Auth for admin
  adminAuth(req, res, async (err) => { // Make this callback async
    if (err) {
      return next(err);
    }
    // Check if basic auth was successful and user is authenticated
    // The basic-auth library attaches `req.auth` if successful
    // We need to manually populate req.user for basic auth users
    if (req.auth && req.auth.user) {
      // For basic auth, we assume it's an admin if it passed adminAuth
      // In a more robust system, we would fetch user details from DB
      // For now, we'll create a dummy admin user
      req.user = { id: req.auth.user, username: req.auth.user, role: 'admin' };
      return next();
    }
    
    res.setHeader('WWW-Authenticate', 'Bearer realm="Protected", Basic realm="AdminArea"');
    return res.status(401).send('Authentication required.');
  });
};

export const authorizeRoles = (roles: Array<User['role']>) => {
  return (req: Request, res: Response, next: NextFunction) => {
    if (!req.user) {
      return res.status(401).json({ error: 'Authentication required for this action.' });
    }
    if (!roles.includes(req.user.role)) {
      return res.status(403).json({ error: `Forbidden: ${req.user.role} does not have access.` });
    }
    next();
  };
};
