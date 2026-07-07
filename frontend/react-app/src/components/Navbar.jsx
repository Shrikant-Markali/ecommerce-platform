import { Link, useNavigate } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import { useState } from 'react'
import { logout } from '../store/authSlice'
import { clearCart } from '../store/cartSlice'
import authService from '../services/authService'
import toast from 'react-hot-toast'
import { ShoppingCart, User, LogOut, LayoutDashboard, Package } from 'lucide-react'

const Navbar = () => {
  const navigate = useNavigate()
  const dispatch = useDispatch()
  const { isAuthenticated, user } = useSelector((state) => state.auth)
  const { totalItems } = useSelector((state) => state.cart)
  const [searchKeyword, setSearchKeyword] = useState('')
  const [isMenuOpen, setIsMenuOpen] = useState(false)

  const isAdmin = user?.roles?.includes('ROLE_ADMIN')

  const handleLogout = async () => {
    try {
      await authService.logout()
    } catch (error) {
      // Continue logout even if API fails
    }
    dispatch(logout())
    dispatch(clearCart())
    toast.success('Logged out successfully')
    navigate('/')
  }

  const handleSearch = (e) => {
    e.preventDefault()
    if (searchKeyword.trim()) {
      navigate(`/?search=${searchKeyword}`)
    }
  }

  return (
    <nav className="bg-blue-600 text-white shadow-lg">
      <div className="max-w-7xl mx-auto px-4 py-3">
        <div className="flex items-center justify-between">

          {/* Logo */}
          <Link to="/" className="text-xl font-bold text-white">
            🛒 ECommerce
          </Link>

          {/* Search Bar */}
          <form onSubmit={handleSearch} className="flex-1 mx-8">
            <div className="flex">
              <input
                type="text"
                placeholder="Search products..."
                value={searchKeyword}
                onChange={(e) => setSearchKeyword(e.target.value)}
                className="w-full px-4 py-2 rounded-l-lg text-gray-800 focus:outline-none"
              />
              <button
                type="submit"
                className="bg-yellow-400 text-gray-800 px-4 py-2 rounded-r-lg font-semibold hover:bg-yellow-500"
              >
                Search
              </button>
            </div>
          </form>

          {/* Right Side */}
          <div className="flex items-center gap-4">

            {/* Cart Icon */}
            {isAuthenticated && (
              <Link to="/cart" className="relative">
                <ShoppingCart size={24} />
                {totalItems > 0 && (
                  <span className="absolute -top-2 -right-2 bg-yellow-400 text-gray-800 text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center">
                    {totalItems}
                  </span>
                )}
              </Link>
            )}

            {/* Auth Buttons */}
            {!isAuthenticated ? (
              <div className="flex gap-2">
                <Link
                  to="/login"
                  className="bg-white text-blue-600 px-4 py-2 rounded-lg font-semibold hover:bg-gray-100"
                >
                  Login
                </Link>
                <Link
                  to="/register"
                  className="bg-yellow-400 text-gray-800 px-4 py-2 rounded-lg font-semibold hover:bg-yellow-500"
                >
                  Register
                </Link>
              </div>
            ) : (
              <div className="relative">
                <button
                  onClick={() => setIsMenuOpen(!isMenuOpen)}
                  className="flex items-center gap-2 bg-blue-700 px-3 py-2 rounded-lg hover:bg-blue-800"
                >
                  <User size={18} />
                  <span>{user?.firstName}</span>
                </button>

                {/* Dropdown Menu */}
                {isMenuOpen && (
                  <div className="absolute right-0 mt-2 w-48 bg-white text-gray-800 rounded-lg shadow-lg z-50">
                    <Link
                      to="/orders"
                      className="flex items-center gap-2 px-4 py-3 hover:bg-gray-100"
                      onClick={() => setIsMenuOpen(false)}
                    >
                      <Package size={16} />
                      My Orders
                    </Link>

                    {isAdmin && (
                      <Link
                        to="/admin"
                        className="flex items-center gap-2 px-4 py-3 hover:bg-gray-100"
                        onClick={() => setIsMenuOpen(false)}
                      >
                        <LayoutDashboard size={16} />
                        Admin Dashboard
                      </Link>
                    )}

                    <button
                      onClick={handleLogout}
                      className="flex items-center gap-2 px-4 py-3 hover:bg-gray-100 w-full text-left text-red-600"
                    >
                      <LogOut size={16} />
                      Logout
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  )
}

export default Navbar