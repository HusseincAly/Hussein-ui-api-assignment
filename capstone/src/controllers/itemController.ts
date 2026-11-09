// src/controllers/itemController.ts
import { Request, Response, NextFunction } from 'express';
import { itemService } from '../services/itemService';
import { validate } from '../validation/validator';
import { itemSchema } from '../validation/schemas';
import ApiError from '../utils/ApiError';

class ItemController {
  async create(req: Request, res: Response, next: NextFunction) {
    try {
      // Validation is now handled by a middleware that throws ApiError if validation fails
      await validate(itemSchema)(req, res, () => {}); // Call validate middleware
      const newItem = await itemService.create(req.body);
      res.status(201).json(newItem);
    } catch (error) {
      next(error); // Pass error to global error handler
    }
  }

  async list(req: Request, res: Response, next: NextFunction) {
    try {
      const items = await itemService.findAll();
      res.status(200).json(items);
    } catch (error) {
      next(error);
    }
  }

  async get(req: Request, res: Response, next: NextFunction) {
    try {
      const id = Array.isArray(req.params.id) ? req.params.id[0] : req.params.id;
      if (!id) {
        throw new ApiError(400, 'Item ID is required.');
      }
      const item = await itemService.findById(id); // findById now throws ApiError(404)
      res.status(200).json(item);
    } catch (error) {
      next(error);
    }
  }

  async update(req: Request, res: Response, next: NextFunction) {
    try {
      const id = Array.isArray(req.params.id) ? req.params.id[0] : req.params.id;
      if (!id) {
        throw new ApiError(400, 'Item ID is required.');
      }
      await validate(itemSchema)(req, res, () => {}); // Call validate middleware
      const updatedItem = await itemService.update(id, req.body); // update now throws ApiError(404)
      res.status(200).json(updatedItem);
    } catch (error) {
      next(error);
    }
  }

  async delete(req: Request, res: Response, next: NextFunction) {
    try {
      const id = Array.isArray(req.params.id) ? req.params.id[0] : req.params.id;
      if (!id) {
        throw new ApiError(400, 'Item ID is required.');
      }
      await itemService.delete(id); // delete now throws ApiError(404)
      res.status(204).send();
    } catch (error) {
      next(error);
    }
  }
}

export const itemController = new ItemController();
