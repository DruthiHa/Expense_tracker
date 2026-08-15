require("dotenv").config();
const express = require("express");
const mongoose = require("mongoose");
const cors = require("cors");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");

// Models
const User = require("./models/User");
const Expense = require("./models/Expense");

// Middleware
const auth = require("./middleware/authMiddleware");

const app = express();
app.use(cors());
app.use(express.json());

// -------------------------
// MongoDB Connection
// -------------------------
mongoose
  .connect(process.env.MONGO_URI)
  .then(() => console.log("MongoDB connected"))
  .catch((err) => console.error("MongoDB connection error:", err));

// -------------------------
// Auth Routes
// -------------------------

// Signup
app.post("/api/auth/signup", async (req, res) => {
  try {
    const { name, email, password } = req.body;

    const existing = await User.findOne({ email });
    if (existing) return res.json({ msg: "User already exists" });

    const hashed = await bcrypt.hash(password, 10);
    const user = new User({ name, email, password: hashed });

    await user.save();

    res.json({ msg: "Signup successful" });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Login
app.post("/api/auth/login", async (req, res) => {
  try {
    const { email, password } = req.body;

    const user = await User.findOne({ email });
    if (!user) return res.json({ msg: "User not found" });

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) return res.json({ msg: "Invalid password" });

    const token = jwt.sign(
      { id: user._id, email: user.email },
      process.env.JWT_SECRET,
      { expiresIn: "24h" }
    );

    res.json({
      msg: "Login successful",
      token,
      user: { id: user._id, name: user.name, email: user.email },
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// -------------------------
// GET LOGGED-IN USER
// -------------------------
app.get("/api/auth/me", auth, async (req, res) => {
  try {
    const user = await User.findById(req.user.id).select("-password");
    res.json(user);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// -------------------------
// Expense Routes (Protected)
// -------------------------

// Get user expenses
app.get("/api/expenses", auth, async (req, res) => {
  try {
    const expenses = await Expense.find({ user: req.user.id }).sort({ date: -1 });
    res.json(expenses);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Add new expense
app.post("/api/expenses", auth, async (req, res) => {
  try {
    const exp = new Expense({
      ...req.body,
      user: req.user.id,
    });

    await exp.save();
    res.status(201).json(exp);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});

// Update expense
app.put("/api/expenses/:id", auth, async (req, res) => {
  try {
    const exp = await Expense.findOneAndUpdate(
      { _id: req.params.id, user: req.user.id },
      req.body,
      { new: true }
    );
    res.json(exp);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});

// Delete expense
app.delete("/api/expenses/:id", auth, async (req, res) => {
  try {
    await Expense.findOneAndDelete({ _id: req.params.id, user: req.user.id });
    res.json({ deleted: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// -------------------------
// Start Server
// -------------------------
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on ${PORT}`));
