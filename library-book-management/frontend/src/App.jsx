import { useEffect, useState } from 'react'
import axios from 'axios'

const api = axios.create({ baseURL: 'https://library-book-management-3-z1ri.onrender.com/api' })
const emptyForm = { bookId: '', title: '', author: '', category: '', status: 'AVAILABLE' }

export default function App() {
  const [books, setBooks] = useState([])
  const [form, setForm] = useState(emptyForm)
  const [editingId, setEditingId] = useState(null)
  const [search, setSearch] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function loadBooks(term = search) {
    try {
      setLoading(true)
      const response = await api.get('/books', { params: term.trim() ? { search: term.trim() } : {} })
      setBooks(response.data)
      setError('')
    } catch {
      setError('Could not connect to the server. Make sure the Spring Boot backend is running.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { loadBooks('') }, [])

  function changeField(event) {
    setForm({ ...form, [event.target.name]: event.target.value })
  }

  async function submit(event) {
    event.preventDefault()
    setError('')
    try {
      if (editingId) {
        await api.put(`/books/${encodeURIComponent(editingId)}`, form)
      } else {
        await api.post('/books', form)
      }
      setForm(emptyForm)
      setEditingId(null)
      await loadBooks()
    } catch (e) {
      setError(e.response?.data?.message || 'Unable to save this book. Please check the details.')
    }
  }

  function editBook(book) {
    setEditingId(book.bookId)
    setForm({ ...book })
    setError('')
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  function cancelEdit() {
    setEditingId(null)
    setForm(emptyForm)
    setError('')
  }

  async function removeBook(book) {
    if (!window.confirm(`Delete "${book.title}"?`)) return
    try {
      await api.delete(`/books/${encodeURIComponent(book.bookId)}`)
      await loadBooks()
    } catch (e) {
      setError(e.response?.data?.message || 'Unable to delete this book.')
    }
  }

  async function toggleStatus(book) {
    const status = book.status === 'AVAILABLE' ? 'ISSUED' : 'AVAILABLE'
    try {
      await api.patch(`/books/${encodeURIComponent(book.bookId)}/availability`, { status })
      await loadBooks()
    } catch (e) {
      setError(e.response?.data?.message || 'Unable to change availability.')
    }
  }

  return (
    <main className="page">
      <header>
        <h1>Library Book Management</h1>
        <p>Add, find, update, and manage library books.</p>
      </header>

      <section className="panel">
        <h2>{editingId ? 'Edit Book' : 'Add a Book'}</h2>
        <form onSubmit={submit} className="book-form">
          <label>Book ID
            <input name="bookId" value={form.bookId} onChange={changeField} required maxLength="40"
              disabled={Boolean(editingId)} placeholder="e.g. BK-101" />
          </label>
          <label>Title
            <input name="title" value={form.title} onChange={changeField} required maxLength="150" placeholder="Book title" />
          </label>
          <label>Author
            <input name="author" value={form.author} onChange={changeField} required maxLength="120" placeholder="Author name" />
          </label>
          <label>Category
            <input name="category" value={form.category} onChange={changeField} required maxLength="80" placeholder="e.g. Fiction" />
          </label>
          <div className="form-actions">
            <button type="submit">{editingId ? 'Save Changes' : 'Add Book'}</button>
            {editingId && <button type="button" className="secondary" onClick={cancelEdit}>Cancel</button>}
          </div>
        </form>
        {error && <p className="error" role="alert">{error}</p>}
      </section>

      <section className="panel">
        <div className="list-heading">
          <h2>Books</h2>
          <input className="search" aria-label="Search books" value={search}
            onChange={e => { setSearch(e.target.value); loadBooks(e.target.value) }}
            placeholder="Search by title or author..." />
        </div>
        {loading ? <p>Loading books...</p> : books.length === 0 ? (
          <p className="empty">No books found. Add a book using the form above.</p>
        ) : (
          <div className="table-wrap">
            <table>
              <thead><tr><th>Book ID</th><th>Title</th><th>Author</th><th>Category</th><th>Status</th><th>Actions</th></tr></thead>
              <tbody>
                {books.map(book => (
                  <tr key={book.bookId}>
                    <td>{book.bookId}</td><td>{book.title}</td><td>{book.author}</td><td>{book.category}</td>
                    <td><span className={`status ${book.status === 'AVAILABLE' ? 'available' : 'issued'}`}>
                      {book.status === 'AVAILABLE' ? 'Available' : 'Issued'}
                    </span></td>
                    <td className="actions">
                      <button className="small secondary" onClick={() => editBook(book)}>Edit</button>
                      <button className="small" onClick={() => toggleStatus(book)}>
                        Mark {book.status === 'AVAILABLE' ? 'Issued' : 'Available'}
                      </button>
                      <button className="small danger" onClick={() => removeBook(book)}>Delete</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
      <footer>Library Book Management · React + Spring Boot</footer>
    </main>
  )
}
