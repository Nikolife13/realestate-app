const API = '/api';

function getPropertyId() {
    const params = new URLSearchParams(window.location.search);
    return params.get('id');
}

async function loadDetail() {
    const id = getPropertyId();
    if (!id) {
        document.getElementById('propertyDetail').innerHTML = '<p>Invalid property ID.</p>';
        return;
    }
    try {
        const res = await fetch(`${API}/properties/${id}`);
        const prop = await res.json();
        renderDetail(prop);
        setupGallery(prop.imageUrls || []);
    } catch(e) {
        document.getElementById('propertyDetail').innerHTML = '<p>Error loading property.</p>';
    }
}

function renderDetail(prop) {
    const container = document.getElementById('propertyDetail');
    container.innerHTML = `
        <h1 class="property-title">${prop.title}</h1>
        <div class="property-address">📍 ${prop.address}, ${prop.city}, ${prop.county}</div>
        <div class="property-price">$${prop.price.toLocaleString()}</div>
        <div>
            <span class="property-rooms">🛏️ ${prop.rooms} rooms</span>
            <span class="property-bathrooms">🚽 ${prop.bathrooms} bathrooms</span>
        </div>
        <p><strong>Description:</strong> ${prop.description || 'No description provided.'}</p>
        <h3>Photo Gallery</h3>
        <div id="galleryContainer" class="gallery"></div>
    `;
}

function setupGallery(imageUrls) {
    const galleryDiv = document.getElementById('galleryContainer');
    if (!imageUrls || imageUrls.length === 0) {
        galleryDiv.innerHTML = '<p>No additional images.</p>';
        return;
    }
    galleryDiv.innerHTML = imageUrls.map(url => `<img src="${url}" alt="gallery image" class="gallery-img">`).join('');
    
    const modal = document.getElementById('fullscreenModal');
    const modalImg = document.getElementById('fullscreenImage');
    const closeSpan = document.querySelector('.close-fullscreen');
    
    document.querySelectorAll('.gallery-img').forEach(img => {
        img.addEventListener('click', () => {
            modal.style.display = 'block';
            modalImg.src = img.src;
        });
    });
    closeSpan.onclick = () => modal.style.display = 'none';
    modal.onclick = (e) => { if (e.target === modal) modal.style.display = 'none'; };
}

document.getElementById('backBtn')?.addEventListener('click', () => {
    window.location.href = 'dashboard.html';
});

loadDetail();