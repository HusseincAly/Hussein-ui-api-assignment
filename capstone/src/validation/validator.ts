// src/validation/validator.ts
import { Request, Response, NextFunction } from 'express';
import Joi from 'joi';
import ApiError from '../utils/ApiError';

export const validate = (schema: Joi.Schema, source: 'body' | 'params' | 'query' = 'body') => {
  return (req: Request, res: Response, next: NextFunction) => {
    const { error, value } = schema.validate(req[source], { abortEarly: false });

    if (error) {
      const errors = error.details.map(detail => detail.message);
      throw new ApiError(400, errors.join('; ')); // Throw ApiError
    }

    // Attach validated data to the request object
    req[source] = value;
    next();
  };
};
