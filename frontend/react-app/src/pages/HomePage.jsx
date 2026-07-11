import { useEffect, useState, useRef  } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate, useSearchParams } from 'react-router-dom'
import {
  setProducts,
  setCategories,
  setProductLoading,
  setSelectedCategory,
} from '../store/productSlice'
import productService from '../services/productService'
import orderService from '../services/orderService'
import { setCart } from '../store/cartSlice'
import toast from 'react-hot-toast'
import { ShoppingCart, Search } from 'lucide-react'

const HomePage = () => {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const { products, categories, loading, selectedCategory } = useSelector(
    (state) => state.products
  )
  const { isAuthenticated } = useSelector((state) => state.auth)
  const [minPrice, setMinPrice] = useState('')
  const [maxPrice, setMaxPrice] = useState('')
  const addingRef = useRef({})

  // Fetch categories on mount
  useEffect(() => {
    fetchCategories()
  }, [])

  // Fetch products when filters change
  useEffect(() => {
    const searchKeyword = searchParams.get('search')
    if (searchKeyword) {
      searchProducts(searchKeyword)
    } else {
      fetchProducts()
    }
  }, [searchParams, selectedCategory])

  const fetchCategories = async () => {
    try {
      const response = await productService.getAllCategories()
      if (response.success) {
        dispatch(setCategories(response.data))
      }
    } catch (error) {
      console.error('Failed to fetch categories')
    }
  }

  const fetchProducts = async () => {
    dispatch(setProductLoading(true))
    try {
      let response
      if (selectedCategory) {
        response = await productService.getProductsByCategory(selectedCategory)
      } else {
        response = await productService.getAllProducts()
      }
      if (response.success) {
        dispatch(setProducts(response.data))
      }
    } catch (error) {
      console.error('Failed to fetch products')
    }
  }

  const searchProducts = async (keyword) => {
    dispatch(setProductLoading(true))
    try {
      const response = await productService.searchProducts(keyword)
      if (response.success) {
        dispatch(setProducts(response.data))
      }
    } catch (error) {
      console.error('Failed to search products')
    }
  }

  const handlePriceFilter = async () => {
    if (!minPrice || !maxPrice) {
      toast.error('Please enter both min and max price')
      return
    }
    dispatch(setProductLoading(true))
    try {
      const response = await productService.filterProducts(
        selectedCategory,
        minPrice,
        maxPrice
      )
      if (response.success) {
        dispatch(setProducts(response.data))
      }
    } catch (error) {
      console.error('Failed to filter products')
    }
  }

  const handleAddToCart = async (productId) => {
  if (!isAuthenticated) {
    toast.error('Please login to add items to cart')
    navigate('/login')
    return
  }

  // Prevent multiple clicks
  if (addingRef.current[productId]) return
  addingRef.current[productId] = true

  try {
    const response = await orderService.addToCart(productId, 1)
    if (response.success) {
      dispatch(setCart(response.data))
      toast.success('Added to cart!')
    }
  } catch (error) {
    toast.error('Failed to add to cart')
  } finally {
    setTimeout(() => {
      addingRef.current[productId] = false
    }, 2000)
  }
} 

  return (
    <div className="max-w-7xl mx-auto px-4 py-6">
      <div className="flex gap-6">

        {/* Sidebar — Categories & Filters */}
        <div className="w-64 flex-shrink-0">

          {/* Categories */}
          <div className="bg-white rounded-xl shadow p-4 mb-4">
            <h3 className="font-bold text-gray-800 mb-3">Categories</h3>
            <ul className="space-y-2">
              <li>
                <button
                  onClick={() => dispatch(setSelectedCategory(null))}
                  className={`w-full text-left px-3 py-2 rounded-lg text-sm ${
                    !selectedCategory
                      ? 'bg-blue-600 text-white'
                      : 'hover:bg-gray-100 text-gray-700'
                  }`}
                >
                  All Products
                </button>
              </li>
              {categories.map((category) => (
                <li key={category.id}>
                  <button
                    onClick={() => dispatch(setSelectedCategory(category.id))}
                    className={`w-full text-left px-3 py-2 rounded-lg text-sm ${
                      selectedCategory === category.id
                        ? 'bg-blue-600 text-white'
                        : 'hover:bg-gray-100 text-gray-700'
                    }`}
                  >
                    {category.name}
                  </button>
                </li>
              ))}
            </ul>
          </div>

          {/* Price Filter */}
          <div className="bg-white rounded-xl shadow p-4">
            <h3 className="font-bold text-gray-800 mb-3">Price Range (₹)</h3>
            <div className="space-y-3">
              <input
                type="number"
                placeholder="Min Price"
                value={minPrice}
                onChange={(e) => setMinPrice(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <input
                type="number"
                placeholder="Max Price"
                value={maxPrice}
                onChange={(e) => setMaxPrice(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <button
                onClick={handlePriceFilter}
                className="w-full bg-blue-600 text-white py-2 rounded-lg text-sm font-semibold hover:bg-blue-700"
              >
                Apply Filter
              </button>
              <button
                onClick={() => {
                  setMinPrice('')
                  setMaxPrice('')
                  fetchProducts()
                }}
                className="w-full bg-gray-100 text-gray-700 py-2 rounded-lg text-sm font-semibold hover:bg-gray-200"
              >
                Clear Filter
              </button>
            </div>
          </div>
        </div>

        {/* Main Content — Products Grid */}
        <div className="flex-1">

          {/* Search Results Header */}
          {searchParams.get('search') && (
            <div className="mb-4 flex items-center gap-2">
              <Search size={18} className="text-gray-500" />
              <p className="text-gray-600">
                Search results for:{' '}
                <span className="font-semibold">"{searchParams.get('search')}"</span>
              </p>
            </div>
          )}

          {/* Loading */}
          {loading ? (
            <div className="flex justify-center items-center h-64">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            </div>
          ) : products.length === 0 ? (
            <div className="text-center py-20">
              <p className="text-gray-500 text-xl">No products found</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
              {products.map((product) => (
                <div
                  key={product.id}
                  className="bg-white rounded-xl shadow hover:shadow-md transition cursor-pointer"
                >
                  {/* Product Image */}
                  <div
                    onClick={() => navigate(`/products/${product.id}`)}
                    className="h-48 bg-gray-100 rounded-t-xl flex items-center justify-center overflow-hidden"
                  >
                    {product.imageUrl ? (
                      <img
                        src={product.imageUrl}
                        alt={product.name}
                        className="h-full w-full object-cover"
                        onError={(e) => {
                          e.target.src = 'https://via.placeholder.com/200'
                        }}
                      />
                    ) : (
                      <div className="text-gray-400 text-4xl">📦</div>
                    )}
                  </div>

                  {/* Product Info */}
                  <div className="p-4">
                    <p className="text-xs text-blue-600 font-medium mb-1">
                      {product.categoryName}
                    </p>
                    <h3
                      onClick={() => navigate(`/products/${product.id}`)}
                      className="font-semibold text-gray-800 text-sm mb-1 hover:text-blue-600 line-clamp-2"
                    >
                      {product.name}
                    </h3>

                    {/* Rating */}
                    <div className="flex items-center gap-1 mb-2">
                      <span className="text-yellow-400 text-xs">★</span>
                      <span className="text-xs text-gray-500">
                        {product.rating} ({product.totalReviews} reviews)
                      </span>
                    </div>

                    {/* Price */}
                    <p className="text-lg font-bold text-gray-900 mb-3">
                      ₹{product.price?.toLocaleString('en-IN')}
                    </p>

                    {/* Stock */}
                    <p className={`text-xs mb-3 ${
                      product.stockQuantity > 0 ? 'text-green-600' : 'text-red-500'
                    }`}>
                      {product.stockQuantity > 0
                        ? `In Stock (${product.stockQuantity})`
                        : 'Out of Stock'}
                    </p>

                    {/* Add to Cart Button */}
                    <button
                        onClick={() => handleAddToCart(product.id)}
                        disabled={product.stockQuantity === 0}
                        className="w-full flex items-center justify-center gap-2 bg-yellow-400 text-gray-800 py-2 rounded-lg text-sm font-semibold hover:bg-yellow-500 disabled:opacity-50 disabled:cursor-not-allowed transition"
                      >
                        <ShoppingCart size={16} />
                        Add to Cart
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default HomePage