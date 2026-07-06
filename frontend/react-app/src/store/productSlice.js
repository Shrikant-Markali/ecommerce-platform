import { createSlice } from '@reduxjs/toolkit'

const initialState = {
  products: [],
  categories: [],
  selectedProduct: null,
  loading: false,
  error: null,
  searchKeyword: '',
  selectedCategory: null,
}

const productSlice = createSlice({
  name: 'products',
  initialState,
  reducers: {
    setProducts: (state, action) => {
      state.products = action.payload
      state.loading = false
    },
    setCategories: (state, action) => {
      state.categories = action.payload
    },
    setSelectedProduct: (state, action) => {
      state.selectedProduct = action.payload
    },
    setProductLoading: (state, action) => {
      state.loading = action.payload
    },
    setProductError: (state, action) => {
      state.error = action.payload
      state.loading = false
    },
    setSearchKeyword: (state, action) => {
      state.searchKeyword = action.payload
    },
    setSelectedCategory: (state, action) => {
      state.selectedCategory = action.payload
    },
  },
})

export const {
  setProducts,
  setCategories,
  setSelectedProduct,
  setProductLoading,
  setProductError,
  setSearchKeyword,
  setSelectedCategory,
} = productSlice.actions

export default productSlice.reducer