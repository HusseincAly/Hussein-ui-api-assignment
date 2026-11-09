import { Router } from 'express';
import { jsonController } from './controllers/jsonController';
import { authController } from './controllers/authController';
import { authMiddleware, authorizeRoles } from './middleware/authMiddleware'; // Import authorizeRoles
import { itemController } from './controllers/itemController'; // Import itemController
import { orderController } from './controllers/orderController'; // Import orderController

const router = Router();

// Auth route
router.post('/login', authController.login);

// Protected JSON routes (existing)
router.use('/json', authMiddleware);
router.post('/json', jsonController.create);
router.get('/json/:id', jsonController.get);
router.put('/json/:id', jsonController.update);
router.patch('/json/:id', jsonController.patch);
router.delete('/json/:id', jsonController.delete);

// Item Routes
// List items (Customer and Admin)
router.get('/items', authMiddleware, itemController.list);
// Get single item (Customer and Admin)
router.get('/items/:id', authMiddleware, itemController.get);
// Create items (Admin only)
router.post('/items', authMiddleware, authorizeRoles(['admin']), itemController.create);
// Update item (Admin only)
router.put('/items/:id', authMiddleware, authorizeRoles(['admin']), itemController.update);
// Delete item (Admin only)
router.delete('/items/:id', authMiddleware, authorizeRoles(['admin']), itemController.delete);

// Order Routes
// Create orders (Admin only)
router.post('/orders', authMiddleware, authorizeRoles(['admin']), orderController.createOrder);
// List orders (Customer and Admin) - customer can only list their own, admin can list all
router.get('/orders', authMiddleware, orderController.listOrders);
// Delete order (Admin only)
router.delete('/orders/:id', authMiddleware, authorizeRoles(['admin']), orderController.deleteOrder);
// List paid orders (Admin only)
router.get('/orders/paid', authMiddleware, authorizeRoles(['admin']), orderController.listPaidOrders);
// Checkout items (Customer only)
router.post('/orders/:id/checkout', authMiddleware, authorizeRoles(['customer']), orderController.checkoutOrder);


export { router };
