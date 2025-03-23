function addToCart(productId) {
    const count = document.getElementById('quantity-' + productId);
    const data = {
        productId: productId,
        count: parseInt(count.value)
    };
    const url = `/carts/add`;
    console.log(url);

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            return response.json().then(body => {
                if (!response.ok) {
                    alert(body.message);
                    throw new Error(`Error: ${response.status} - ${body.message || 'Unknown error'}`);
                }
                alert(body.message);
                return body;
            });
        })
        .catch(error => {
            console.error('Ошибка:', error);
        });
}
function removeFromCart(productId) {
    const count = document.getElementById('quantity-' + productId);
    const data = {
        productId: productId,
        count: parseInt(count.value)
    };
    const url = `/carts/deleteItem`;
    console.log(url);

    fetch(url, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            return response.json().then(body => {
                if (!response.ok) {
                    alert(body.message);
                    throw new Error(`Error: ${response.status} - ${body.message || 'Unknown error'}`);
                }
                alert(body.message);
            });
        })
        .catch(error => {
            console.error('Ошибка:', error);
        });
}

function increaseQuantity(productId) {
    const input = document.getElementById('quantity-' + productId); // Находим поле ввода по уникальному ID
    input.value = parseInt(input.value) + 1; // Увеличиваем значение на 1
}

function decreaseQuantity(productId) {
    const input = document.getElementById('quantity-' + productId);
    let quantity = parseInt(input.value);
    if (quantity > 1) {
        input.value = quantity - 1;
    }
}
function applyFilters() {
    const search = document.getElementById('search').value;
    const size = document.getElementById('page-size').value;
    const priceSort = document.getElementById('price-filter').value;
    const alphabetSort = document.getElementById('alphabet-filter').value;
    let url = `/products?page=0&size=${size}`;
    if (search) url += `&search=${encodeURIComponent(search)}`;
    if (priceSort) url += `&sort=${encodeURIComponent(priceSort)}`;
    if (alphabetSort) url += `&sort=${encodeURIComponent(alphabetSort)}`;
    window.location.href = url;
}

document.addEventListener('DOMContentLoaded', () => {
    const pageSizeSelect = document.getElementById('page-size');
    const urlParams = new URLSearchParams(window.location.search);
    const sizeParam = urlParams.get('size');

    if (sizeParam && pageSizeSelect) {
        pageSizeSelect.value = sizeParam;
    }
});