function deleteProduct(productId) {

    if (!confirm("Вы уверены, что хотите удалить этот продукт?")) {
        return; // Если пользователь отменяет действие, выходим из функции
    }

    const url = `/admin/products/${productId}`;

    fetch(url, {
        method: 'DELETE',
    })
        .then(response => {
            if (response.ok) {
                const row = document.querySelector(`tr[data-product-id="${productId}"]`);
                if (row) {
                    row.remove();
                }
            } else {
                alert('Ошибка при удалении продукта. Попробуйте снова.');
            }
        })
        .catch(error => {
            console.error('Ошибка:', error);
            alert('Произошла ошибка при удалении продукта.');
        });
}