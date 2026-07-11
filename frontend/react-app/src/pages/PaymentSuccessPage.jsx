import { useNavigate } from 'react-router-dom'
import { CheckCircle } from 'lucide-react'

const PaymentSuccessPage = () => {
  const navigate = useNavigate()

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="bg-white rounded-xl shadow-lg p-8 text-center max-w-md">
        <CheckCircle size={64} className="text-green-500 mx-auto mb-4" />
        <h1 className="text-2xl font-bold text-gray-800 mb-2">
          Payment Successful!
        </h1>
        <p className="text-gray-500 mb-6">
          Your order has been confirmed and payment received.
        </p>
        <button
          onClick={() => navigate('/orders')}
          className="bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700 mr-3"
        >
          View Orders
        </button>
        <button
          onClick={() => navigate('/')}
          className="bg-gray-100 text-gray-700 px-6 py-3 rounded-lg font-semibold hover:bg-gray-200"
        >
          Continue Shopping
        </button>
      </div>
    </div>
  )
}

export default PaymentSuccessPage