const express = require("express");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const User = require("../models/User");

const router = express.Router();

// SIGNUP
router.post("/signup", async (req, res) => {
    try {
        const { name, email, password } = req.body;

        const exists = await User.findOne({ email });
        if (exists) return res.status(400).json({ msg: "User already exists" });

        const hashed = await bcrypt.hash(password, 10);

        const newUser = new User({ name, email, password: hashed });
        await newUser.save();

        return res.json({ msg: "Signup successful" });

    } catch (err) {
        console.log(err);
        res.status(500).json({ msg: "Internal server error" });
    }
});

// LOGIN
router.post("/login", async (req, res) => {
    try {
        const { email, password } = req.body;

        const user = await User.findOne({ email });
        if (!user) return res.status(400).json({ msg: "Invalid email or password" });

        const valid = await bcrypt.compare(password, user.password);
        if (!valid) return res.status(400).json({ msg: "Invalid email or password" });

        // Create JWT token
        const token = jwt.sign(
            { id: user._id, email: user.email },
            process.env.JWT_SECRET,
            { expiresIn: "1d" }
        );

        res.json({
            msg: "Login successful",
            token,
            user: { id: user._id, name: user.name, email: user.email }
        });

    } catch (err) {
        console.log(err);
        res.status(500).json({ msg: "Internal server error" });
    }
});

module.exports = router;
