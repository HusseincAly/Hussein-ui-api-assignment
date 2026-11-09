import { Request, Response, NextFunction } from 'express';
import { authService } from '../services/authService';
import { validate } from '../validation/validator';
import { loginSchema } from '../validation/schemas';
import ApiError from '../utils/ApiError';

class AuthController {
  async login(req: Request, res: Response, next: NextFunction) {
    try {
      await validate(loginSchema)(req, res, () => {});
      const { username, password } = req.body;
      const token = await authService.generateToken(username, password);
      if (token) {
        res.json({ token });
      } else {
        throw new ApiError(401, 'Invalid credentials');
      }
    } catch (error) {
      next(error);
    }
  }
}

export const authController = new AuthController();
