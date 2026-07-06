import axiosInstance from '../utils/axiosConfig'

const orderService = {

  getCart: async () => {
    const response = await axiosInstance.get('/api/v1/cart')
    return response.data
  },

  addToCart: async (productId, quantity) => {
    const response = await axiosInstance.post('/api/v1/cart/add', {
      productId,
      quantity,
    })
    return response.data
  },

  updateCartItem: async (cartItemId, quantity) => {
    const response = await axiosInstance.put(`/api/v1/cart/update/${cartItemId}`, {
      quantity,
    })
    return response.data
  },

  removeFromCart: async (cartItemId) => {
    const response = await axiosInstance.delete(`/api/v1/cart/remove/${cartItemId}`)
    return response.data
  },

  clearCart: async () => {
    const response = await axiosInstance.delete('/api/v1/cart/clear')
    return response.data
  },

  placeOrder: async (shippingAddress) => {
    const response = await axiosInstance.post('/api/v1/orders', {
      shippingAddress,
    })
    return response.data
  },

  getMyOrders: async () => {
    const response = await axiosInstance.get('/api/v1/orders')
    return response.data
  },

  getOrderById: async (orderId) => {
    const response = await axiosInstance.get(`/api/v1/orders/${orderId}`)
    return response.data
  },

  cancelOrder: async (orderId) => {
    const response = await axiosInstance.put(`/api/v1/orders/${orderId}/cancel`)
    return response.data
  },
}

export default orderService