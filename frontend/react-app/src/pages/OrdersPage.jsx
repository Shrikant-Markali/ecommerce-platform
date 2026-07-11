import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import orderService from '../services/orderService'
import toast from 'react-hot-toast'
import { Package, ChevronDown, ChevronUp, XCircle } from 'lucide-react'

const statusColors = {
  PENDING: 'bg-yellow-100 text-yellow-800',
  CONFIRMED: 'bg-blue-100 text-blue-800',
  SHIPPED: 'bg-purple-100 text-purple-800',
  DELIVERED: 'bg-green-100 text-green-800',
  CANCELLED: 'bg-red-100 text-red-800',
}

const OrdersPage = () => {
  const navigate = useNavigate()
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [expandedOrder, setExpandedOrder] = useState(null)

  useEffect(() => {
    fetchOrders()
  }, [])

  const fetchOrders = async () => {
    setLoading(true)
    try {
      const response = await orderService.getMyOrders()
      if (response.success) {
        setOrders(response.data)
      }
    } catch (error) {
      toast.error('Failed to fetch orders')
    } finally {
      setLoading(false)
    }
  }

  const handleCancelOrder = async (orderId) => {
    try {
      const response = await orderService.cancelOrder(orderId)
      if (response.success) {
        toast.success('Order cancelled successfully')
        fetchOrders()
      }
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to cancel order')
    }
  }

  const toggleExpand = (orderId) => {
    setExpandedOrder(expandedOrder === orderId ? null : orderId)
  }

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-6">

      {/* Header */}
      <div className="flex items-center gap-3 mb-6">
        <Package size={24} className="text-blue-600" />
        <h1 className="text-2xl font-bold text-gray-800">My Orders</h1>
      </div>

      {/* Empty State */}
      {orders.length === 0 ? (
        <div className="text-center py-20">
          <Package size={64} className="text-gray-300 mx-auto mb-4" />
          <h2 className="text-xl font-semibold text-gray-500 mb-4">
            No orders yet!
          </h2>
          <button
            onClick={() => navigate('/')}
            className="bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700"
          >
            Start Shopping
          </button>
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map((order) => (
            <div key={order.id} className="bg-white rounded-xl shadow overflow-hidden">

              {/* Order Header */}
              <div
                className="p-4 flex items-center justify-between cursor-pointer hover:bg-gray-50"
                onClick={() => toggleExpand(order.id)}
              >
                <div className="flex items-center gap-4">
                  <div>
                    <p className="font-bold text-gray-800">Order #{order.id}</p>
                    <p className="text-sm text-gray-500">
                      {new Date(order.createdAt).toLocaleDateString('en-IN', {
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric'
                      })}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-4">
                  {/* Status Badge */}
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold ${statusColors[order.status]}`}>
                    {order.status}
                  </span>

                  {/* Total */}
                  <p className="font-bold text-gray-800">
                    ₹{order.totalAmount?.toLocaleString('en-IN')}
                  </p>

                  {/* Expand Icon */}
                  {expandedOrder === order.id
                    ? <ChevronUp size={18} className="text-gray-500" />
                    : <ChevronDown size={18} className="text-gray-500" />
                  }
                </div>
              </div>

              {/* Order Details (Expanded) */}
              {expandedOrder === order.id && (
                <div className="border-t px-4 pb-4">

                  {/* Shipping Address */}
                  <div className="py-3 border-b">
                    <p className="text-sm text-gray-500">Shipping Address</p>
                    <p className="text-gray-800 font-medium">{order.shippingAddress}</p>
                  </div>

                  {/* Order Items */}
                  <div className="py-3 space-y-3">
                    {order.items?.map((item) => (
                      <div key={item.id} className="flex items-center justify-between">
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
                            <p className="text-gray-500 text-xs">Qty: {item.quantity} × ₹{item.price?.toLocaleString('en-IN')}</p>
                          </div>
                        </div>
                        <p className="font-semibold text-gray-800">
                          ₹{item.subtotal?.toLocaleString('en-IN')}
                        </p>
                      </div>
                    ))}
                  </div>

                  {/* Cancel Button */}
                  {order.status === 'PENDING' && (
                    <button
                      onClick={() => handleCancelOrder(order.id)}
                      className="flex items-center gap-2 text-red-500 hover:text-red-600 text-sm font-medium mt-2"
                    >
                      <XCircle size={16} />
                      Cancel Order
                    </button>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default OrdersPage