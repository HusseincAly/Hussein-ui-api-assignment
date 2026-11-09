import express, { Request, Response, NextFunction } from 'express';
import { json } from 'body-parser';
import { router } from './routes';
import ApiError from './utils/ApiError'; // Import ApiError

const app = express();

// Global API Timeout Middleware (30 seconds)
app.use((req: Request, res: Response, next: NextFunction) => {
  req.setTimeout(30 * 1000, () => { // 30 seconds
    const err: any = new Error('Request Timeout');
    err.status = 408; // 408 Request Timeout
    next(err);
  });
  next();
});

// 1. JSON body parsing middleware with custom error handling for invalid JSON
app.use(json());

app.use((err: Error, req: Request, res: Response, next: NextFunction) => {
  if (err instanceof SyntaxError && 'body' in err) {
    console.error('Invalid JSON received:', err.message);
    return res.status(400).json({ error: 'Invalid JSON payload received.' });
  }
  next(err);
});

// Use the main router
app.use(router);

// 2. Handle 404 Not Found (and implicitly 405 for existing paths with unsupported methods by Express)
// This middleware catches requests that fall through all other routes
app.use((req: Request, res: Response, next: NextFunction) => {
  const err: any = new Error(`Cannot ${req.method} ${req.originalUrl}`);
  err.status = 404;
  next(err);
});


// 3. Centralized Error Handling Middleware (Express-specific)
app.use((err: any, req: Request, res: Response, next: NextFunction) => {
  // If headers have already been sent, delegate to default Express error handler
  if (res.headersSent) {
    return next(err);
  }

  // Log the error
  console.error('API Error:', err.message);
  if (process.env.NODE_ENV !== 'production') {
    console.error(err.stack); // Log stack in dev
  }

  let statusCode = 500;
  let errorMessage = 'Internal Server Error';

  if (err instanceof ApiError) {
    statusCode = err.status;
    errorMessage = err.message;
  } else if (err.status) { // For errors with a 'status' property (e.g., from req.setTimeout or 404 handler)
    statusCode = err.status;
    errorMessage = err.message;
  }

  // For 500 errors, hide specific error messages in production
  if (statusCode >= 500 && process.env.NODE_ENV === 'production') {
    errorMessage = 'Internal Server Error';
  }

  res.status(statusCode).json({ error: errorMessage });
});


const port = process.env.PORT || 3000;

const server = app.listen(port, () => {
  console.log(`IBM Intern API Server is running on port ${port}`);
});

// Global unhandled promise rejection handler - only for truly critical issues leading to shutdown
process.on('unhandledRejection', (reason: Error | any, promise: Promise<any>) => {
  console.error('CRITICAL: Unhandled Rejection at:', promise, 'reason:', reason);
  // Log the stack trace for critical errors
  if (reason instanceof Error) {
    console.error(reason.stack);
  }
  // A robust application might attempt to gracefully shutdown here
  server.close(() => {
    process.exit(1); // Exit with a failure code
  });
});

// Global uncaught exception handler - only for truly critical issues leading to shutdown
process.on('uncaughtException', (error: Error) => {
  console.error('CRITICAL: Uncaught Exception:', error.message);
  console.error(error.stack);
  // A robust application might attempt to gracefully shutdown here
  server.close(() => {
    process.exit(1); // Exit with a failure code
  });
});