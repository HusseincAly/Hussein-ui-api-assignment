import jwt from 'jsonwebtoken';
import fs from 'fs/promises';
import path from 'path';
import basicAuth from 'express-basic-auth';
import { User } from '../types'; // Import the User interface

const usersFilePath = path.join(__dirname, '../../users.json');
const JWT_SECRET = 'your_jwt_secret'; // In a real app, use an environment variable

class AuthService {
  private async getUsers(): Promise<any[]> {
    try {
      const data = await fs.readFile(usersFilePath, 'utf-8');
      return JSON.parse(data);
    } catch (error) {
      console.error('Error reading users file:', error);
      return [];
    }
  }

  public async generateToken(username: string, password: string): Promise<string | null> {
    const users = await this.getUsers();
    const user = users.find((u: any) => u.username === username && u.password === password);

    if (user) {
      const role = user.isAdmin ? 'admin' : 'customer';
      const payload: User = {
        id: user.id || username, // Assuming 'id' exists or use username as fallback
        username: user.username,
        role: role,
      };
      return jwt.sign(payload, JWT_SECRET, { expiresIn: '1h' });
    }

    return null;
  }

  public verifyToken(token: string): User | null {
    try {
      const decoded = jwt.verify(token, JWT_SECRET) as User;
      return decoded;
    } catch (error) {
      return null;
    }
  }

  public getAdminBasicAuth() {
    return basicAuth({
      authorizer: this.adminAuthorizer.bind(this),
      authorizeAsync: true,
      challenge: true,
      realm: 'AdminArea',
    });
  }

  private async adminAuthorizer(username: string, password: string, cb: (err: any, authorized?: boolean) => void) {
    const users = await this.getUsers();
    const adminUser = users.find((u: any) => u.username === username && u.password === password && u.isAdmin);
    
    if (adminUser) {
        // Attach user info to the request for Basic Auth as well
        // This part needs to be handled in the middleware that uses getAdminBasicAuth
        // For now, just return authorized true
      return cb(null, true);
    } else {
      return cb(null, false);
    }
  }
}

export const authService = new AuthService();
