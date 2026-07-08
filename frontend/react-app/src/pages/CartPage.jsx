import { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import { setCart, clearCart } from '../store/cartSlice'
import orderService from '../services/orderService'
import toast from 'react-hot-toast'
import { Trash2, Plus, Minus, ShoppingBag, ArrowLeft } from 'lucide-react'

const CartPage = () => {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { items, totalAmount, totalItems } = useSelector((state) => state.cart)
  const [loading, setLoading] = useState(true)
  const [updatingItem, setUpdatingItem] = useState(null)

  useEffect(() => {
    fetchCart()
  }, [])

  const fetchCart = async () => {
    setLoading(true)
    try {
      const response = await orderService.getCart()
      if (response.success) {
        dispatch(setCart(response.data))
      }
    } catch (error) {
      toast.error('Failed to fetch cart')
    } finally {
      setLoading(false)
    }
  }

  const handleUpdateQuantity = async (cartItemId, newQuantity) => {
    if (newQuantity < 1) return
    setUpdatingItem(cartItemId)
    try {
      const response = await orderService.updateCartItem(cartItemId, newQuantity)
      if (response.success) {
        dispatch(setCart(response.data))
      }
    } catch (error) {
      toast.error('Failed to update quantity')
    } finally {
      setUpdatingItem(null)
    }
  }

  const handleRemoveItem = async (cartItemId) => {
    setUpdatingItem(cartItemId)
    try {
      const response = await orderService.removeFromCart(cartItemId)
      if (response.success) {
        dispatch(setCart(response.data))
        toast.success('Item removed from cart')
      }
    } catch (error) {
      toast.error('Failed to remove item')
    } finally {
      setUpdatingItem(null)
    }
  }

  const handleClearCart = async () => {
    try {
      await orderService.clearCart()
      dispatch(clearCart())
      toast.success('Cart cleared')
    } catch (error) {
      toast.error('Failed to clear cart')
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
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-3">
          <button
            onClick={() => navigate('/')}
            className="flex items-center gap-2 text-blue-600 hover:text-blue-700"
          >
            <ArrowLeft size={18} />
            Continue Shopping
          </button>
          <h1 className="text-2xl font-bold text-gray-800">
            Shopping Cart ({totalItems} items)
          </h1>
        </div>
        {items.length > 0 && (
          <button
            onClick={handleClearCart}
            className="text-red-500 hover:text-red-600 text-sm font-medium"
          >
            Clear Cart
          </button>
        )}
      </div>

      {/* Empty Cart */}
      {items.length === 0 ? (
        <div className="text-center py-20">
          <ShoppingBag size={64} className="text-gray-300 mx-auto mb-4" />
          <h2 className="text-xl font-semibold text-gray-500 mb-4">
            Your cart is empty!
          </h2>
          <button
            onClick={() => navigate('/')}
            className="bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700"
          >
            Start Shopping
          </button>
        </div>
      ) : (
        <div className="flex flex-col lg:flex-row gap-6">

          {/* Cart Items */}
          <div className="flex-1 space-y-4">
            {items.map((item) => (
              <div
                key={item.id}
                className="bg-white rounded-xl shadow p-4 flex gap-4"
              >
                {/* Product Image */}
                <div className="w-24 h-24 bg-gray-100 rounded-lg flex-shrink-0 overflow-hidden">
                  {item.imageUrl ? (
                    <img
                      src={item.imageUrl}
                      alt={item.productName}
                      className="w-full h-full object-cover"
                      onError={(e) => {
                        e.target.src = 'https://via.placeholder.com/100'
                      }}
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center text-2xl">
                      📦
                    </div>
                  )}
                </div>

                {/* Product Info */}
                <div className="flex-1">
                  <h3 className="font-semibold text-gray-800 mb-1">
                    {item.productName}
                  </h3>
                  <p className="text-blue-600 font-bold text-lg">
                    ₹{item.price?.toLocaleString('en-IN')}
                  </p>
                  <p className="text-gray-500 text-sm">
                    Subtotal: ₹{item.subtotal?.toLocaleString('en-IN')}
                  </p>
                </div>

                {/* Quantity Controls */}
                <div className="flex flex-col items-end justify-between">
                  <button
                    onClick={() => handleRemoveItem(item.id)}
                    disabled={updatingItem === item.id}
                    className="text-red-400 hover:text-red-600 transition"
                  >
                    <Trash2 size={18} />
                  </button>

                  <div className="flex items-center border border-gray-300 rounded-lg">
                    <button
                      onClick={() => handleUpdateQuantity(item.id, item.quantity - 1)}
                      disabled={updatingItem === item.id || item.quantity <= 1}
                      className="px-3 py-1 hover:bg-gray-100 disabled:opacity-50"
                    >
                      <Minus size={14} />
                    </button>
                    <span className="px-3 py-1 font-semibold text-sm">
                      {item.quantity}
                    </span>
                    <button
                      onClick={() => handleUpdateQuantity(item.id, item.quantity + 1)}
                      disabled={updatingItem === item.id}
                      className="px-3 py-1 hover:bg-gray-100 disabled:opacity-50"
                    >
                      <Plus size={14} />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Order Summary */}
          <div className="lg:w-80">
            <div className="bg-white rounded-xl shadow p-6 sticky top-4">
              <h2 className="text-xl font-bold text-gray-800 mb-4">
                Order Summary
              </h2>

              <div className="space-y-3 mb-4">
                <div className="flex justify-between text-gray-600">
                  <span>Items ({totalItems})</span>
                  <span>₹{totalAmount?.toLocaleString('en-IN')}</span>
                </div>
                <div className="flex justify-between text-gray-600">
                  <span>Shipping</span>
                  <span className="text-green-600">FREE</span>
                </div>
                <div className="border-t pt-3 flex justify-between font-bold text-lg">
                  <span>Total</span>
                  <span>₹{totalAmount?.toLocaleString('en-IN')}</span>
                </div>
              </div>

              <button
                onClick={() => navigate('/checkout')}
                className="w-full bg-yellow-400 text-gray-800 py-3 rounded-xl font-bold text-lg hover:bg-yellow-500 transition"
              >
                Proceed to Checkout
              </button>

              <button
                onClick={() => navigate('/')}
                className="w-full mt-3 text-blue-600 py-2 text-sm hover:underline"
              >
                Continue Shopping
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default CartPage