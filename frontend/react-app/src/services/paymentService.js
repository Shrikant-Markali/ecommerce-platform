import axiosInstance from '../utils/axiosConfig'

const paymentService = {

  createPayment: async (orderId) => {
    const response = await axiosInstance.post('/api/v1/payments/create', {
      orderId,
    })
    return response.data
  },

  getPaymentByOrderId: async (orderId) => {
    const response = await axiosInstance.get(`/api/v1/payments/order/${orderId}`)
    return response.data
  },

  getMyPayments: async () => {
    const response = await axiosInstance.get('/api/v1/payments/my-payments')
    return response.data
  },
}

export default paymentService