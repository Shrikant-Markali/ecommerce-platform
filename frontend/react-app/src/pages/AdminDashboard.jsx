import { useState, useEffect } from 'react'
import productService from '../services/productService'
import orderService from '../services/orderService'
import authService from '../services/authService'
import toast from 'react-hot-toast'
import {
  LayoutDashboard, Package, ShoppingBag, Users,
  Plus, Edit, Trash2, X
} from 'lucide-react'
import axiosInstance from '../utils/axiosConfig'

const AdminDashboard = () => {
  const [activeTab, setActiveTab] = useState('products')
  const [products, setProducts] = useState([])
  const [categories, setCategories] = useState([])
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)

  // Product Modal
  const [showProductModal, setShowProductModal] = useState(false)
  const [editingProduct, setEditingProduct] = useState(null)
  const [productForm, setProductForm] = useState({
    name: '', description: '', price: '', stockQuantity: '',
    categoryId: '', imageUrl: ''
  })

  // Category Modal
  const [showCategoryModal, setShowCategoryModal] = useState(false)
  const [categoryForm, setCategoryForm] = useState({
    name: '', description: '', imageUrl: ''
  })

  useEffect(() => {
    fetchAllData()
  }, [])

  const fetchAllData = async () => {
    setLoading(true)
    try {
      const [productsRes, categoriesRes, ordersRes] = await Promise.all([
        productService.getAllProducts(),
        productService.getAllCategories(),
        orderService.getMyOrders(),
      ])
      if (productsRes.success) setProducts(productsRes.data)
      if (categoriesRes.success) setCategories(categoriesRes.data)

      // Get ALL orders (admin)
      const allOrdersRes = await axiosInstance.get('/api/v1/orders/admin/all')
      if (allOrdersRes.data.success) setOrders(allOrdersRes.data.data)
    } catch (error) {
      toast.error('Failed to load dashboard data')
    } finally {
      setLoading(false)
    }
  }

  // ============================================
  // PRODUCT HANDLERS
  // ============================================

  const openAddProduct = () => {
    setEditingProduct(null)
    setProductForm({
      name: '', description: '', price: '', stockQuantity: '',
      categoryId: '', imageUrl: ''
    })
    setShowProductModal(true)
  }

  const openEditProduct = (product) => {
    setEditingProduct(product)
    setProductForm({
      name: product.name,
      description: product.description,
      price: product.price,
      stockQuantity: product.stockQuantity,
      categoryId: product.categoryId,
      imageUrl: product.imageUrl || ''
    })
    setShowProductModal(true)
  }

  const handleProductSubmit = async (e) => {
    e.preventDefault()
    try {
      if (editingProduct) {
        await axiosInstance.put(
          `/api/v1/admin/products/${editingProduct.id}`,
          { ...productForm, price: parseFloat(productForm.price), stockQuantity: parseInt(productForm.stockQuantity), categoryId: parseInt(productForm.categoryId) }
        )
        toast.success('Product updated successfully')
      } else {
        await axiosInstance.post(
          '/api/v1/admin/products',
          { ...productForm, price: parseFloat(productForm.price), stockQuantity: parseInt(productForm.stockQuantity), categoryId: parseInt(productForm.categoryId) }
        )
        toast.success('Product created successfully')
      }
      setShowProductModal(false)
      fetchAllData()
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to save product')
    }
  }

  const handleDeleteProduct = async (productId) => {
    if (!confirm('Are you sure you want to delete this product?')) return
    try {
      await axiosInstance.delete(`/api/v1/admin/products/${productId}`)
      toast.success('Product deleted successfully')
      fetchAllData()
    } catch (error) {
      toast.error('Failed to delete product')
    }
  }

  // ============================================
  // CATEGORY HANDLERS
  // ============================================

  const handleCategorySubmit = async (e) => {
    e.preventDefault()
    try {
      await axiosInstance.post('/api/v1/admin/categories', categoryForm)
      toast.success('Category created successfully')
      setShowCategoryModal(false)
      setCategoryForm({ name: '', description: '', imageUrl: '' })
      fetchAllData()
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to create category')
    }
  }

  // ============================================
  // ORDER HANDLERS
  // ============================================

  const handleUpdateOrderStatus = async (orderId, newStatus) => {
    try {
      await axiosInstance.put(
        `/api/v1/orders/admin/${orderId}/status?status=${newStatus}`
      )
      toast.success('Order status updated')
      fetchAllData()
    } catch (error) {
      toast.error('Failed to update order status')
    }
  }

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-6">

      {/* Header */}
      <div className="flex items-center gap-3 mb-6">
        <LayoutDashboard size={24} className="text-blue-600" />
        <h1 className="text-2xl font-bold text-gray-800">Admin Dashboard</h1>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
        <div className="bg-white rounded-xl shadow p-6">
          <div className="flex items-center gap-3">
            <div className="bg-blue-100 p-3 rounded-lg">
              <Package className="text-blue-600" size={24} />
            </div>
            <div>
              <p className="text-gray-500 text-sm">Total Products</p>
              <p className="text-2xl font-bold text-gray-800">{products.length}</p>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-xl shadow p-6">
          <div className="flex items-center gap-3">
            <div className="bg-green-100 p-3 rounded-lg">
              <ShoppingBag className="text-green-600" size={24} />
            </div>
            <div>
              <p className="text-gray-500 text-sm">Total Orders</p>
              <p className="text-2xl font-bold text-gray-800">{orders.length}</p>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-xl shadow p-6">
          <div className="flex items-center gap-3">
            <div className="bg-purple-100 p-3 rounded-lg">
              <Users className="text-purple-600" size={24} />
            </div>
            <div>
              <p className="text-gray-500 text-sm">Categories</p>
              <p className="text-2xl font-bold text-gray-800">{categories.length}</p>
            </div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-2 mb-6 border-b">
        <button
          onClick={() => setActiveTab('products')}
          className={`px-4 py-2 font-medium ${activeTab === 'products' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500'}`}
        >
          Products
        </button>
        <button
          onClick={() => setActiveTab('categories')}
          className={`px-4 py-2 font-medium ${activeTab === 'categories' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500'}`}
        >
          Categories
        </button>
        <button
          onClick={() => setActiveTab('orders')}
          className={`px-4 py-2 font-medium ${activeTab === 'orders' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500'}`}
        >
          Orders
        </button>
      </div>

      {/* Products Tab */}
      {activeTab === 'products' && (
        <div>
          <div className="flex justify-end mb-4">
            <button
              onClick={openAddProduct}
              className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg font-semibold hover:bg-blue-700"
            >
              <Plus size={18} />
              Add Product
            </button>
          </div>

          <div className="bg-white rounded-xl shadow overflow-hidden">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="text-left px-4 py-3 text-sm font-semibold text-gray-600">Product</th>
                  <th className="text-left px-4 py-3 text-sm font-semibold text-gray-600">Category</th>
                  <th className="text-left px-4 py-3 text-sm font-semibold text-gray-600">Price</th>
                  <th className="text-left px-4 py-3 text-sm font-semibold text-gray-600">Stock</th>
                  <th className="text-left px-4 py-3 text-sm font-semibold text-gray-600">Actions</th>
                </tr>
              </thead>
              <tbody>
                {products.map((product) => (
                  <tr key={product.id} className="border-t">
                    <td className="px-4 py-3 text-sm text-gray-800">{product.name}</td>
                    <td className="px-4 py-3 text-sm text-gray-600">{product.categoryName}</td>
                    <td className="px-4 py-3 text-sm text-gray-800">₹{product.price?.toLocaleString('en-IN')}</td>
                    <td className="px-4 py-3 text-sm text-gray-600">{product.stockQuantity}</td>
                    <td className="px-4 py-3">
                      <div className="flex gap-2">
                        <button onClick={() => openEditProduct(product)} className="text-blue-600 hover:text-blue-700">
                          <Edit size={16} />
                        </button>
                        <button onClick={() => handleDeleteProduct(product.id)} className="text-red-500 hover:text-red-600">
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Categories Tab */}
      {activeTab === 'categories' && (
        <div>
          <div className="flex justify-end mb-4">
            <button
              onClick={() => setShowCategoryModal(true)}
              className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg font-semibold hover:bg-blue-700"
            >
              <Plus size={18} />
              Add Category
            </button>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            {categories.map((category) => (
              <div key={category.id} className="bg-white rounded-xl shadow p-4">
                <h3 className="font-bold text-gray-800">{category.name}</h3>
                <p className="text-gray-500 text-sm mt-1">{category.description}</p>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Orders Tab */}
      {activeTab === 'orders' && (
        <div className="bg-white rounded-xl shadow overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="text-left px-4 py-3 text-sm font-semibold text-gray-600">Order ID</th>
                <th className="text-left px-4 py-3 text-sm font-semibold text-gray-600">Amount</th>
                <th className="text-left px-4 py-3 text-sm font-semibold text-gray-600">Status</th>
                <th className="text-left px-4 py-3 text-sm font-semibold text-gray-600">Update Status</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <tr key={order.id} className="border-t">
                  <td className="px-4 py-3 text-sm text-gray-800">#{order.id}</td>
                  <td className="px-4 py-3 text-sm text-gray-800">₹{order.totalAmount?.toLocaleString('en-IN')}</td>
                  <td className="px-4 py-3 text-sm">
                    <span className="px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-xs font-semibold">
                      {order.status}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <select
                      onChange={(e) => handleUpdateOrderStatus(order.id, e.target.value)}
                      defaultValue=""
                      className="border border-gray-300 rounded-lg px-2 py-1 text-sm"
                    >
                      <option value="" disabled>Change status</option>
                      <option value="CONFIRMED">Confirmed</option>
                      <option value="SHIPPED">Shipped</option>
                      <option value="DELIVERED">Delivered</option>
                      <option value="CANCELLED">Cancelled</option>
                    </select>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Product Modal */}
      {showProductModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-lg p-6 w-full max-w-md max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-lg font-bold text-gray-800">
                {editingProduct ? 'Edit Product' : 'Add Product'}
              </h2>
              <button onClick={() => setShowProductModal(false)}>
                <X size={20} className="text-gray-500" />
              </button>
            </div>
            <form onSubmit={handleProductSubmit} className="space-y-3">
              <input
                type="text"
                placeholder="Product Name"
                value={productForm.name}
                onChange={(e) => setProductForm({...productForm, name: e.target.value})}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              />
              <textarea
                placeholder="Description"
                value={productForm.description}
                onChange={(e) => setProductForm({...productForm, description: e.target.value})}
                required
                rows={3}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              />
              <input
                type="number"
                placeholder="Price"
                value={productForm.price}
                onChange={(e) => setProductForm({...productForm, price: e.target.value})}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              />
              <input
                type="number"
                placeholder="Stock Quantity"
                value={productForm.stockQuantity}
                onChange={(e) => setProductForm({...productForm, stockQuantity: e.target.value})}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              />
              <select
                value={productForm.categoryId}
                onChange={(e) => setProductForm({...productForm, categoryId: e.target.value})}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              >
                <option value="">Select Category</option>
                {categories.map((cat) => (
                  <option key={cat.id} value={cat.id}>{cat.name}</option>
                ))}
              </select>
              <input
                type="text"
                placeholder="Image URL"
                value={productForm.imageUrl}
                onChange={(e) => setProductForm({...productForm, imageUrl: e.target.value})}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              />
              <button
                type="submit"
                className="w-full bg-blue-600 text-white py-2 rounded-lg font-semibold hover:bg-blue-700"
              >
                {editingProduct ? 'Update Product' : 'Create Product'}
              </button>
            </form>
          </div>
        </div>
      )}

      {/* Category Modal */}
      {showCategoryModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-lg p-6 w-full max-w-md">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-lg font-bold text-gray-800">Add Category</h2>
              <button onClick={() => setShowCategoryModal(false)}>
                <X size={20} className="text-gray-500" />
              </button>
            </div>
            <form onSubmit={handleCategorySubmit} className="space-y-3">
              <input
                type="text"
                placeholder="Category Name"
                value={categoryForm.name}
                onChange={(e) => setCategoryForm({...categoryForm, name: e.target.value})}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              />
              <textarea
                placeholder="Description"
                value={categoryForm.description}
                onChange={(e) => setCategoryForm({...categoryForm, description: e.target.value})}
                rows={3}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              />
              <input
                type="text"
                placeholder="Image URL"
                value={categoryForm.imageUrl}
                onChange={(e) => setCategoryForm({...categoryForm, imageUrl: e.target.value})}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
              />
              <button
                type="submit"
                className="w-full bg-blue-600 text-white py-2 rounded-lg font-semibold hover:bg-blue-700"
              >
                Create Category
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

export default AdminDashboard