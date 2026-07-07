import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useDispatch, useSelector } from 'react-redux'
import { setSelectedProduct } from '../store/productSlice'
import { setCart } from '../store/cartSlice'
import productService from '../services/productService'
import orderService from '../services/orderService'
import toast from 'react-hot-toast'
import { ShoppingCart, ArrowLeft, Star, Package } from 'lucide-react'

const ProductDetailPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const dispatch = useDispatch()
  const { selectedProduct } = useSelector((state) => state.products)
  const { isAuthenticated } = useSelector((state) => state.auth)
  const [loading, setLoading] = useState(true)
  const [quantity, setQuantity] = useState(1)
  const [addingToCart, setAddingToCart] = useState(false)

  useEffect(() => {
    fetchProduct()
  }, [id])

  const fetchProduct = async () => {
    setLoading(true)
    try {
      const response = await productService.getProductById(id)
      if (response.success) {
        dispatch(setSelectedProduct(response.data))
      }
    } catch (error) {
      toast.error('Product not found')
      navigate('/')
    } finally {
      setLoading(false)
    }
  }

  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      toast.error('Please login to add items to cart')
      navigate('/login')
      return
    }
    setAddingToCart(true)
    try {
      const response = await orderService.addToCart(selectedProduct.id, quantity)
      if (response.success) {
        dispatch(setCart(response.data))
        toast.success('Added to cart!')
      }
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to add to cart')
    } finally {
      setAddingToCart(false)
    }
  }

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  if (!selectedProduct) return null

  return (
    <div className="max-w-7xl mx-auto px-4 py-6">

      {/* Back Button */}
      <button
        onClick={() => navigate(-1)}
        className="flex items-center gap-2 text-blue-600 hover:text-blue-700 mb-6"
      >
        <ArrowLeft size={18} />
        Back to Products
      </button>

      <div className="bg-white rounded-xl shadow-lg p-6">
        <div className="flex flex-col md:flex-row gap-8">

          {/* Product Image */}
          <div className="md:w-1/2">
            <div className="h-80 bg-gray-100 rounded-xl flex items-center justify-center overflow-hidden">
              {selectedProduct.imageUrl ? (
                <img
                  src={selectedProduct.imageUrl}
                  alt={selectedProduct.name}
                  className="h-full w-full object-cover rounded-xl"
                  onError={(e) => {
                    e.target.src = 'https://via.placeholder.com/400'
                  }}
                />
              ) : (
                <div className="text-gray-400 text-6xl">📦</div>
              )}
            </div>
          </div>

          {/* Product Info */}
          <div className="md:w-1/2">

            {/* Category */}
            <p className="text-blue-600 font-medium text-sm mb-2">
              {selectedProduct.categoryName}
            </p>

            {/* Name */}
            <h1 className="text-2xl font-bold text-gray-800 mb-3">
              {selectedProduct.name}
            </h1>

            {/* Rating */}
            <div className="flex items-center gap-2 mb-4">
              <div className="flex items-center gap-1 bg-green-500 text-white px-2 py-1 rounded text-sm">
                <Star size={14} fill="white" />
                <span>{selectedProduct.rating}</span>
              </div>
              <span className="text-gray-500 text-sm">
                {selectedProduct.totalReviews} reviews
              </span>
            </div>

            {/* Price */}
            <p className="text-3xl font-bold text-gray-900 mb-4">
              ₹{selectedProduct.price?.toLocaleString('en-IN')}
            </p>

            {/* Stock Status */}
            <div className="flex items-center gap-2 mb-4">
              <Package size={16} className={
                selectedProduct.stockQuantity > 0
                  ? 'text-green-600'
                  : 'text-red-500'
              } />
              <p className={`font-medium ${
                selectedProduct.stockQuantity > 0
                  ? 'text-green-600'
                  : 'text-red-500'
              }`}>
                {selectedProduct.stockQuantity > 0
                  ? `In Stock (${selectedProduct.stockQuantity} available)`
                  : 'Out of Stock'}
              </p>
            </div>

            {/* Description */}
            <p className="text-gray-600 mb-6 leading-relaxed">
              {selectedProduct.description}
            </p>

            {/* Quantity Selector */}
            {selectedProduct.stockQuantity > 0 && (
              <div className="flex items-center gap-3 mb-6">
                <span className="text-gray-700 font-medium">Quantity:</span>
                <div className="flex items-center border border-gray-300 rounded-lg">
                  <button
                    onClick={() => setQuantity(Math.max(1, quantity - 1))}
                    className="px-3 py-2 hover:bg-gray-100 text-gray-600 font-bold"
                  >
                    -
                  </button>
                  <span className="px-4 py-2 font-semibold">{quantity}</span>
                  <button
                    onClick={() => setQuantity(
                      Math.min(selectedProduct.stockQuantity, quantity + 1)
                    )}
                    className="px-3 py-2 hover:bg-gray-100 text-gray-600 font-bold"
                  >
                    +
                  </button>
                </div>
              </div>
            )}

            {/* Add to Cart Button */}
            <button
              onClick={handleAddToCart}
              disabled={selectedProduct.stockQuantity === 0 || addingToCart}
              className="w-full flex items-center justify-center gap-2 bg-yellow-400 text-gray-800 py-3 rounded-xl font-bold text-lg hover:bg-yellow-500 disabled:opacity-50 disabled:cursor-not-allowed transition"
            >
              <ShoppingCart size={20} />
              {addingToCart ? 'Adding...' : 'Add to Cart'}
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ProductDetailPage