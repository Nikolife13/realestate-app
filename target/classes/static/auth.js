const API_BASE = 'http://localhost:8080/api';

// Login handler
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        try {
            const res = await fetch(`${API_BASE}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            if (!res.ok) throw new Error('Invalid credentials');
            const data = await res.json();
            localStorage.setItem('token', data.token);
            window.location.href = 'dashboard.html';
        } catch (err) {
            alert('Login failed: ' + err.message);
        }
    });
}

// Register handler
const registerForm = document.getElementById('registerForm');
if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('regUsername').value;
        const password = document.getElementById('regPassword').value;
        const email = document.getElementById('regEmail').value;
        const confirmEmail = document.getElementById('regConfirmEmail').value;
        if (password.length < 6) {
            alert('Password must be at least 6 characters');
            return;
        }
        try {
            const res = await fetch(`${API_BASE}/auth/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password, email, confirmEmail })
            });
            if (!res.ok) {
                const error = await res.text();
                throw new Error(error);
            }
            alert('Registration successful! Please login.');
            window.location.href = 'login.html';
        } catch (err) {
            alert('Registration failed: ' + err.message);
        }
    });
}

// Utility: get auth headers
function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    };
}