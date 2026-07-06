import { createSlice } from '@reduxjs/toolkit'

const initialState = {
  items: [],
  totalAmount: 0,
  totalItems: 0,
  loading: false,
  error: null,
}

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    setCart: (state, action) => {
      state.items = action.payload.items || []
      state.totalAmount = action.payload.totalAmount || 0
      state.totalItems = action.payload.totalItems || 0
    },
    setCartLoading: (state, action) => {
      state.loading = action.payload
    },
    setCartError: (state, action) => {
      state.error = action.payload
      state.loading = false
    },
    clearCart: (state) => {
      state.items = []
      state.totalAmount = 0
      state.totalItems = 0
    },
  },
})

export const {
  setCart,
  setCartLoading,
  setCartError,
  clearCart,
} = cartSlice.actions

export default cartSlice.reducer