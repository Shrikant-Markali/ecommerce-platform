import axiosInstance from '../utils/axiosConfig'

const authService = {

  register: async (userData) => {
    const response = await axiosInstance.post('/api/v1/auth/register', userData)
    return response.data
  },

  login: async (credentials) => {
    const response = await axiosInstance.post('/api/v1/auth/login', credentials)
    return response.data
  },

  logout: async () => {
    const response = await axiosInstance.post('/api/v1/auth/logout')
    return response.data
  },

  getProfile: async () => {
    const response = await axiosInstance.get('/api/v1/users/profile')
    return response.data
  },

  updateProfile: async (profileData) => {
    const response = await axiosInstance.put('/api/v1/users/profile', profileData)
    return response.data
  },
}

export default authService