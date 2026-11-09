import { Request, Response } from 'express';
import { jsonService } from '../services/jsonService';

class JsonController {
  async create(req: Request, res: Response) {
    try {
      const result = await jsonService.create(req.body);
      res.status(201).json(result);
    } catch (error) {
        if (error instanceof Error) {
            res.status(400).json({ error: error.message });
        } else {
            res.status(400).json({ error: 'An unknown error occurred' });
        }
    }
  }

  async get(req: Request, res: Response) {
    try {
      const id = Array.isArray(req.params.id) ? req.params.id[0] : req.params.id;
      if (!id) {
        throw new Error('ID is required');
      }
      const result = await jsonService.get(id);
      res.status(200).json(result);
    } catch (error) {
        if (error instanceof Error) {
            res.status(404).json({ error: error.message });
        } else {
            res.status(404).json({ error: 'An unknown error occurred' });
        }
    }
  }

  async update(req: Request, res: Response) {
    try {
      const id = Array.isArray(req.params.id) ? req.params.id[0] : req.params.id;
      if (!id) {
        throw new Error('ID is required');
      }
      const result = await jsonService.update(id, req.body);
      res.status(200).json(result);
    } catch (error) {
        if (error instanceof Error) {
            res.status(400).json({ error: error.message });
        } else {
            res.status(400).json({ error: 'An unknown error occurred' });
        }
    }
  }

  async patch(req: Request, res: Response) {
    try {
      const id = Array.isArray(req.params.id) ? req.params.id[0] : req.params.id;
      if (!id) {
        throw new Error('ID is required');
      }
      const result = await jsonService.patch(id, req.body);
      res.status(200).json(result);
    } catch (error) {
        if (error instanceof Error) {
            res.status(400).json({ error: error.message });
        } else {
            res.status(400).json({ error: 'An unknown error occurred' });
        }
    }
  }

  async delete(req: Request, res: Response) {
    try {
      const id = Array.isArray(req.params.id) ? req.params.id[0] : req.params.id;
      if (!id) {
        throw new Error('ID is required');
      }
      await jsonService.delete(id);
      res.status(204).send();
    } catch (error) {
        if (error instanceof Error) {
            res.status(404).json({ error: error.message });
        } else {
            res.status(404).json({ error: 'An unknown error occurred' });
        }
    }
  }
}

export const jsonController = new JsonController();
