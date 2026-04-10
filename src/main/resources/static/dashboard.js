const API = '/api';
let currentUserRole = null;
let currentUsername = null;
let editPropertyId = null;
/**
 * Parses the JWT token to extract the payload data
 * @param {string} token - The encoded JWT string
 */

function parseJwt(token) {
    try {
        return JSON.parse(atob(token.split('.')[1]));
    } catch(e) { return null; }
}
/**
 * Generates standard authentication headers including the Bearer token
 * @returns {Object} Headers object
 */

function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    };
}
/**
 * Initializes the main dashboard view, checks authentication, and handles user roles
 */

async function initDashboard() {
    const token = localStorage.getItem('token');
    // Redirect to login if no token is found
    if (!token) {
        window.location.href = 'login.html';
        return;
    }
    const payload = parseJwt(token);
    if (!payload) {
        logout();
        return;
    }
    // Set user global variables
    currentUsername = payload.sub;
    currentUserRole = payload.role;
    document.getElementById('userInfo').innerHTML = `Welcome, ${currentUsername} (${currentUserRole})`;

    // Logic based on user roles
    if (currentUserRole === 'BUSINESS_OWNER') {
        document.getElementById('businessPanel').style.display = 'block';
        loadBusinessStats();
    }

    // Allow Property Owners and Admins to add new properties
    if (currentUserRole === 'USER' || currentUserRole === 'ADMIN') {
        document.getElementById('addPropertyBtn').style.display = 'inline-block';
    }


     /**
     * Fetches counties and handles dynamic city dropdown population
     */
    await loadCountiesAndCities();
    await loadProperties();
    // await setHeroBackground();  // <-- ЗАКОММЕНТИРОВАНО (фон не меняется)
}

async function loadCountiesAndCities() {
    try {
        const res = await fetch(`${API}/locations/counties`);
        const counties = await res.json();
        const countySelect = document.getElementById('propCounty');
        countySelect.innerHTML = '<option value="">Select County</option>';
        counties.forEach(c => {
            const opt = document.createElement('option');
            opt.value = c;
            opt.textContent = c;
            countySelect.appendChild(opt);
        });

        // Event listener to update cities list when a county is selected
        countySelect.addEventListener('change', async () => {
            const county = countySelect.value;
            if (!county) return;
            const cityRes = await fetch(`${API}/locations/cities?county=${encodeURIComponent(county)}`);
            const cities = await cityRes.json();
            const citySelect = document.getElementById('propCity');
            citySelect.innerHTML = '<option value="">Select City</option>';
            cities.forEach(city => {
                const opt = document.createElement('option');
                opt.value = city;
                opt.textContent = city;
                citySelect.appendChild(opt);
            });
        });
    } catch(e) { console.error('Error loading locations', e); }
}

/**
 * Fetches properties from the API based on search filters
 */
async function loadProperties() {
    const params = new URLSearchParams();
    const city = document.getElementById('filterCity').value;
    const county = document.getElementById('filterCounty').value;
    const rooms = document.getElementById('filterRooms').value;
    const minPrice = document.getElementById('filterMinPrice').value;
    const maxPrice = document.getElementById('filterMaxPrice').value;

    // Append search filters if they exist
    if (city) params.append('city', city);
    if (county) params.append('county', county);
    if (rooms) params.append('rooms', rooms);
    if (minPrice) params.append('minPrice', minPrice);
    if (maxPrice) params.append('maxPrice', maxPrice);

    try {
        const res = await fetch(`${API}/properties?${params.toString()}`);
        const properties = await res.json();
        renderProperties(properties);
    } catch(e) { console.error(e); }
}
/**
 * Renders the property cards into the dashboard
 * @param {Array} properties - List of property objects
 */
function renderProperties(properties) {
    const container = document.getElementById('propertiesList');
    if (!properties.length) {
        container.innerHTML = '<p>No properties found.</p>';
        return;
    }
    container.innerHTML = properties.map(prop => `
        <div class="property-card" data-id="${prop.id}">
            <div class="card-image">
                <img src="${prop.mainImageUrl || 'https://via.placeholder.com/400x300?text=No+Image'}" alt="${prop.title}">
            </div>
            <div class="card-info">
                <h3>${prop.title}</h3>
                <div>📍 ${prop.city}, ${prop.county} – ${prop.address}</div>
                <div class="price">$${prop.price.toLocaleString()}</div>
                <div><span class="rooms">🛏️ ${prop.rooms} rooms</span> <span class="rooms">🚽 ${prop.bathrooms} baths</span></div>
                <div class="rating">⭐ Rating: ${prop.rating || 0}</div>
                <div class="property-actions">
                    ${(currentUserRole === 'ADMIN' || (currentUserRole === 'USER' && prop.owner?.username === currentUsername)) ? 
                      `<button class="edit-btn" data-id="${prop.id}">✏️ Edit</button>
                       <button class="delete-btn" data-id="${prop.id}">🗑️ Delete</button>` : ''}
                    ${currentUserRole === 'ADMIN' ? `<button class="boost-btn" data-id="${prop.id}">🚀 Boost rating</button>` : ''}
                </div>
            </div>
        </div>
    `).join('');

    // Handle clicks for property details
    document.querySelectorAll('.property-card').forEach(card => {
        card.addEventListener('click', (e) => {
            if (e.target.classList.contains('edit-btn') || e.target.classList.contains('delete-btn') || e.target.classList.contains('boost-btn')) return;
            const id = card.dataset.id;
            window.location.href = `detail.html?id=${id}`;
        });
    });

    // Handle Edit actions
    document.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.stopPropagation();
            const id = btn.dataset.id;
            openEditModal(id);
        });
    });

    // Handle Delete actions
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            e.stopPropagation();
            const id = btn.dataset.id;
            if (confirm('Delete this property?')) {
                await fetch(`${API}/properties/${id}`, { method: 'DELETE', headers: getAuthHeaders() });
                loadProperties();
            }
        });
    });

    // Handle Admin Boost actions
    document.querySelectorAll('.boost-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            e.stopPropagation();
            const id = btn.dataset.id;
            await fetch(`${API}/properties/${id}/boost`, { method: 'POST', headers: getAuthHeaders() });
            loadProperties();
        });
    });
}

// Modal and Form UI Elements
const modal = document.getElementById('propertyModal');
const closeBtn = document.querySelector('.close');
const addBtn = document.getElementById('addPropertyBtn');
const propertyForm = document.getElementById('propertyForm');
const modalTitle = document.getElementById('modalTitle');

// Open modal for adding a new property
addBtn.onclick = () => {
    editPropertyId = null;
    modalTitle.innerText = 'Add New Property';
    propertyForm.reset();
    modal.style.display = 'block';
};

// Close modal logic
closeBtn.onclick = () => modal.style.display = 'none';
window.onclick = (e) => { if (e.target === modal) modal.style.display = 'none'; };

/**
 * Handles the property form submission for both CREATE and UPDATE operations
 */
propertyForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const propertyData = {
        title: document.getElementById('propTitle').value,
        description: document.getElementById('propDescription').value,
        county: document.getElementById('propCounty').value,
        city: document.getElementById('propCity').value,
        address: document.getElementById('propAddress').value,
        rooms: parseInt(document.getElementById('propRooms').value),
        bathrooms: parseInt(document.getElementById('propBathrooms').value),
        price: parseFloat(document.getElementById('propPrice').value),
        mainImageUrl: document.getElementById('propMainImage').value,
        imageUrls: document.getElementById('propGallery').value.split(',').map(s => s.trim()).filter(s => s)
    };
    const url = editPropertyId ? `${API}/properties/${editPropertyId}` : `${API}/properties`;
    const method = editPropertyId ? 'PUT' : 'POST';
    try {
        const res = await fetch(url, { method, headers: getAuthHeaders(), body: JSON.stringify(propertyData) });
        if (!res.ok) throw new Error('Failed to save');
        modal.style.display = 'none';
        loadProperties();
        // setHeroBackground();  // <-- ЗАКОММЕНТИРОВАНО
    } catch(err) { alert('Error: ' + err.message); }
});

/**
 * Pre-fills and opens the modal to edit an existing property
 * @param {string} id - The property ID
 */
async function openEditModal(id) {
    const res = await fetch(`${API}/properties/${id}`);
    const prop = await res.json();
    editPropertyId = id;
    modalTitle.innerText = 'Edit Property';

    // Fill form fields with property data
    document.getElementById('propTitle').value = prop.title;
    document.getElementById('propDescription').value = prop.description;
    document.getElementById('propCounty').value = prop.county;

    // Trigger change event to populate cities
    const countySelect = document.getElementById('propCounty');
    countySelect.dispatchEvent(new Event('change'));

    // Small timeout to allow city select to populate before setting value
    setTimeout(() => {
        document.getElementById('propCity').value = prop.city;
    }, 100);
    document.getElementById('propAddress').value = prop.address;
    document.getElementById('propRooms').value = prop.rooms;
    document.getElementById('propBathrooms').value = prop.bathrooms;
    document.getElementById('propPrice').value = prop.price;
    document.getElementById('propMainImage').value = prop.mainImageUrl;
    document.getElementById('propGallery').value = prop.imageUrls ? prop.imageUrls.join(', ') : '';
    modal.style.display = 'block';
}

/**
 * Loads registration statistics for business owners using Chart.js
 */
async function loadBusinessStats() {
    try {
        const year = new Date().getFullYear();
        const res = await fetch(`${API}/business/registrations-per-month?year=${year}`, { headers: getAuthHeaders() });
        const data = await res.json();
        const ctx = document.getElementById('statsChart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.map(d => d.month),
                datasets: [{ label: `Registrations in ${year}`, data: data.map(d => d.count), backgroundColor: '#3498db' }]
            }
        });
    } catch(e) { console.error(e); }
}

/**
 * Optional function to dynamically change hero background.
 * Left here for reference but not currently called.
 */
async function setHeroBackground() {
    try {
        const res = await fetch(`${API}/properties`);
        const properties = await res.json();
        const heroSection = document.getElementById('heroSection');
        if (heroSection && properties.length > 0 && properties[0].mainImageUrl) {
            heroSection.style.backgroundImage = `url('${properties[0].mainImageUrl}')`;
        }
    } catch(e) {
        console.error('Failed to set hero background', e);
    }
}

/**
 * Clears local storage and redirects user to login page
 */
function logout() {
    localStorage.removeItem('token');
    window.location.href = 'login.html';
}

// Global Event Listeners
document.getElementById('logoutBtn')?.addEventListener('click', logout);
document.getElementById('searchBtn')?.addEventListener('click', loadProperties);

// Execute Initialization
initDashboard();