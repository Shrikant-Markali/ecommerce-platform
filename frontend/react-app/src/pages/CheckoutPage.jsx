import { useState } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import { clearCart } from '../store/cartSlice'
import orderService from '../services/orderService'
import paymentService from '../services/paymentService'
import toast from 'react-hot-toast'
import { MapPin, ShoppingBag, CreditCard } from 'lucide-react'

const CheckoutPage = () => {
  const navigate = useNavigate()
  const dispatch = useDispatch()
  const { items, totalAmount } = useSelector((state) => state.cart)
  const [shippingAddress, setShippingAddress] = useState('')
  const [loading, setLoading] = useState(false)

  const handleCheckout = async () => {
    if (!shippingAddress.trim()) {
      toast.error('Please enter shipping address')
      return
    }

    if (items.length === 0) {
      toast.error('Your cart is empty')
      return
    }

    setLoading(true)

    try {
      // Step 1: Place order
      const orderResponse = await orderService.placeOrder(shippingAddress)

      if (!orderResponse.success) {
        toast.error('Failed to place order')
        return
      }

      const orderId = orderResponse.data.id
      toast.success('Order placed! Redirecting to PayPal...')

      // Step 2: Create PayPal payment
      const paymentResponse = await paymentService.createPayment(orderId)

      if (!paymentResponse.success) {
        toast.error('Failed to initiate payment')
        return
      }

      // Step 3: Clear cart
      dispatch(clearCart())

      // Step 4: Redirect to PayPal
      const approvalUrl = paymentResponse.data.approvalUrl
      window.location.href = approvalUrl

    } catch (error) {
      toast.error(error.response?.data?.message || 'Checkout failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Checkout</h1>

      <div className="flex flex-col lg:flex-row gap-6">

        {/* Left — Shipping Address */}
        <div className="flex-1 space-y-4">

          {/* Shipping Address */}
          <div className="bg-white rounded-xl shadow p-6">
            <div className="flex items-center gap-2 mb-4">
              <MapPin size={20} className="text-blue-600" />
              <h2 className="text-lg font-bold text-gray-800">
                Shipping Address
              </h2>
            </div>
            <textarea
              value={shippingAddress}
              onChange={(e) => setShippingAddress(e.target.value)}
              placeholder="Enter your full shipping address..."
              rows={4}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
            />
          </div>

          {/* Order Items */}
          <div className="bg-white rounded-xl shadow p-6">
            <div className="flex items-center gap-2 mb-4">
              <ShoppingBag size={20} className="text-blue-600" />
              <h2 className="text-lg font-bold text-gray-800">
                Order Items ({items.length})
              </h2>
            </div>
            <div className="space-y-3">
              {items.map((item) => (
                <div key={item.id} className="flex justify-between items-center py-2 border-b last:border-0">
                  <div className="flex items-center gap-3">
                    <div className="w-12 h-12 bg-gray-100 rounded-lg overflow-hidden">
                      {item.imageUrl ? (
                        <img
                          src={item.imageUrl}
                          alt={item.productName}
                          className="w-full h-full object-cover"
                          onError={(e) => {
                            e.target.src = 'https://placehold.co/50x50?text=No+Image'
                          }}
                        />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center text-lg">📦</div>
                      )}
                    </div>
                    <div>
                      <p className="font-medium text-gray-800 text-sm">{item.productName}</p>
                      <p className="text-gray-500 text-xs">Qty: {item.quantity}</p>
                    </div>
                  </div>
                  <p className="font-semibold text-gray-800">
                    ₹{item.subtotal?.toLocaleString('en-IN')}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Right — Order Summary */}
        <div className="lg:w-80">
          <div className="bg-white rounded-xl shadow p-6 sticky top-4">
            <h2 className="text-lg font-bold text-gray-800 mb-4">
              Order Summary
            </h2>

            <div className="space-y-3 mb-6">
              <div className="flex justify-between text-gray-600">
                <span>Subtotal</span>
                <span>₹{totalAmount?.toLocaleString('en-IN')}</span>
              </div>
              <div className="flex justify-between text-gray-600">
                <span>Shipping</span>
                <span className="text-green-600">FREE</span>
              </div>
              <div className="flex justify-between text-gray-600">
                <span>Tax</span>
                <span>₹0</span>
              </div>
              <div className="border-t pt-3 flex justify-between font-bold text-lg">
                <span>Total</span>
                <span>₹{totalAmount?.toLocaleString('en-IN')}</span>
              </div>
            </div>

            {/* PayPal Button */}
            <button
              onClick={handleCheckout}
              disabled={loading || items.length === 0}
              className="w-full bg-yellow-400 text-gray-800 py-3 rounded-xl font-bold text-lg hover:bg-yellow-500 disabled:opacity-50 disabled:cursor-not-allowed transition flex items-center justify-center gap-2"
            >
              <CreditCard size={20} />
              {loading ? 'Processing...' : 'Pay with PayPal'}
            </button>

            <p className="text-center text-gray-500 text-xs mt-3">
              🔒 Secure payment powered by PayPal
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default CheckoutPage