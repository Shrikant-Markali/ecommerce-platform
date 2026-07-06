import axiosInstance from '../utils/axiosConfig'

const productService = {

  getAllProducts: async () => {
    const response = await axiosInstance.get('/api/v1/products')
    return response.data
  },

  getProductById: async (id) => {
    const response = await axiosInstance.get(`/api/v1/products/${id}`)
    return response.data
  },

  searchProducts: async (keyword) => {
    const response = await axiosInstance.get(`/api/v1/products/search?keyword=${keyword}`)
    return response.data
  },

  getProductsByCategory: async (categoryId) => {
    const response = await axiosInstance.get(`/api/v1/products/category/${categoryId}`)
    return response.data
  },

  filterProducts: async (categoryId, minPrice, maxPrice) => {
    let url = '/api/v1/products/filter?'
    if (categoryId) url += `categoryId=${categoryId}&`
    if (minPrice) url += `minPrice=${minPrice}&`
    if (maxPrice) url += `maxPrice=${maxPrice}`
    const response = await axiosInstance.get(url)
    return response.data
  },

  getAllCategories: async () => {
    const response = await axiosInstance.get('/api/v1/categories')
    return response.data
  },
}

export default productService