
import { useEffect, useState } from "react";
import "./App.css";

function App() {
  const [expenses, setExpenses] = useState([]);
  const [summary, setSummary] = useState({
    total: 0,
    byCategory: [],
  });

  const [form, setForm] = useState({
    title: "",
    amount: "",
    category: "Food",
    date: "",
  });

  const API_URL = "https://expense-tracker-api-db2o.onrender.com/api";

  async function fetchData() {
    const expensesResponse = await fetch(`${API_URL}/expenses`);
    const summaryResponse = await fetch(`${API_URL}/summary`);

    setExpenses(await expensesResponse.json());
    setSummary(await summaryResponse.json());
  }

  useEffect(() => {
    fetchData();
  }, []);

  function handleChange(event) {
    setForm({
      ...form,
      [event.target.name]: event.target.value,
    });
  }

  async function addExpense(event) {
    event.preventDefault();

    const response = await fetch(`${API_URL}/expenses`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        ...form,
        amount: Number(form.amount),
      }),
    });

    if (response.ok) {
      setForm({
        title: "",
        amount: "",
        category: "Food",
        date: "",
      });

      await fetchData();
    } else {
      const error = await response.json();
      alert(JSON.stringify(error.errors || error));
    }
  }

    async function deleteExpense(id) {
    const response = await fetch(`${API_URL}/expenses/${id}`, {
      method: "DELETE",
    });

    if (response.ok) {
      await fetchData();
    } else {
      alert("Unable to delete expense.");
    }
  }

  return (
    <div className="app">
      <h1>Expense Tracker</h1>
      <p>Track your spending, manage your money.</p>

      <form onSubmit={addExpense}>

      <section>
        <h2>Recent Expenses</h2>

      <section>
        <h2>Expense Summary</h2>

        <div>
          <h3>Total Spending</h3>
          <h2>₹{Number(summary.total).toFixed(2)}</h2>
        </div>

        <h3>Spending by Category</h3>

        {summary.byCategory.map((item) => {
          const percentage =
            summary.total > 0
              ? (item.total / summary.total) * 100
              : 0;

          return (
            <div key={item.category}>
              <p>
                {item.category} — ₹
                {Number(item.total).toFixed(2)}
                {" "}({item.count} expenses)
              </p>

              <div
                style={{
                  width: "100%",
                  height: "10px",
                  background: "#e5e7eb",
                  borderRadius: "10px",
                }}
              >
                <div
                  style={{
                    width: `${percentage}%`,
                    height: "10px",
                    background: "#64748b",
                    borderRadius: "10px",
                  }}
                />
              </div>
            </div>
          );
        })}
      </section>


        {expenses.length === 0 ? (
          <p>No expenses added yet.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Title</th>
                <th>Amount</th>
                <th>Category</th>
                <th>Date</th>
                <th>Action</th>
              </tr>
            </thead>

            <tbody>
              {expenses.map((expense) => (
                <tr key={expense.id}>
                  <td>{expense.title}</td>
                  <td>₹{Number(expense.amount).toFixed(2)}</td>
                  <td>{expense.category}</td>
                  <td>{expense.date}</td>
                  <td>
                    <button
  type="button"
  onClick={() => deleteExpense(expense.id)}
>
  Delete
</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

        <h2>Add New Expense</h2>

        <input
          type="text"
          name="title"
          placeholder="Expense title"
          value={form.title}
          onChange={handleChange}
          required
        />

        <input
          type="number"
          name="amount"
          placeholder="Amount"
          min="0.01"
          step="0.01"
          value={form.amount}
          onChange={handleChange}
          required
        />

        <select
          name="category"
          value={form.category}
          onChange={handleChange}
        >
          <option value="Food">Food</option>
          <option value="Travel">Travel</option>
          <option value="Bills">Bills</option>
          <option value="Shopping">Shopping</option>
          <option value="Other">Other</option>
        </select>

        <input
          type="date"
          name="date"
          value={form.date}
          onChange={handleChange}
          required
        />

        <button type="submit">Add Expense</button>
      </form>
    </div>
  );
}

export default App;
