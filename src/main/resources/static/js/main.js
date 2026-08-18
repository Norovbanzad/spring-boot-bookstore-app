(function () {
    'use strict';

    const $ = (sel) => document.querySelector(sel);

    function confirmDelete(title) {
        return window.confirm('Delete "' + title + '"? This cannot be undone.');
    }
    window.confirmDelete = confirmDelete;

    const searchInput = $('#search-input');
    const resultsWrap = $('#book-results');
    const liveHint = $('#live-search-hint');
    const liveTerm = $('#live-search-term');

    if (!searchInput || !resultsWrap || !liveHint || !liveTerm) {
        return;
    }

    let timer = null;
    let activeQuery = '';

    const BASE_URL = searchInput.dataset.searchUrl || '/api/books';

    function escapeHtml(value) {
        const div = document.createElement('div');
        div.textContent = value;
        return div.innerHTML;
    }

    function renderResults(books) {
        if (books.length === 0) {
            resultsWrap.innerHTML =
                '<p class="empty-state">No books match your search.</p>';
            return;
        }

        const rows = books.map(function (book) {
            const badge = book.stock > 0
                ? '<span class="badge badge-stock">' + escapeHtml(book.stock) + ' in stock</span>'
                : '<span class="badge badge-out">Out of stock</span>';
            return '<tr>' +
                '<td><a class="book-link" href="/books/' + book.id + '">' + escapeHtml(book.title) + '</a></td>' +
                '<td>' + escapeHtml(book.author) + '</td>' +
                '<td class="mono">' + escapeHtml(book.isbn) + '</td>' +
                '<td>$' + Number(book.price).toFixed(2) + '</td>' +
                '<td>' + badge + '</td>' +
                '<td class="ta-right">' +
                '<a href="/books/' + book.id + '/edit" class="btn btn-sm btn-outline">Edit</a> ' +
                '</td>' +
                '</tr>';
        }).join('');

        resultsWrap.innerHTML =
            '<p class="results-count">' + books.length + ' book(s) found</p>' +
            '<div class="table-wrap">' +
            '<table class="book-table">' +
            '<thead><tr><th>Title</th><th>Author</th><th>ISBN</th><th>Price</th><th>Stock</th><th class="ta-right">Actions</th></tr></thead>' +
            '<tbody>' + rows + '</tbody>' +
            '</table></div>';
    }

    function debouncedSearch() {
        const term = searchInput.value.trim();
        window.clearTimeout(timer);
        if (term.length < 2) {
            liveHint.hidden = true;
            if (activeQuery !== '') {
                activeQuery = '';
                window.location.href = '/books';
            }
            return;
        }
        timer = window.setTimeout(function () {
            activeQuery = term;
            liveTerm.textContent = term;
            liveHint.hidden = false;
            fetch(BASE_URL + '?q=' + encodeURIComponent(term), { headers: { 'Accept': 'application/json' } })
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error('Request failed');
                    }
                    return response.json();
                })
                .then(renderResults)
                .catch(function () {
                    resultsWrap.innerHTML =
                        '<p class="empty-state">Something went wrong while searching.</p>';
                });
        }, 300);
    }

    searchInput.addEventListener('input', debouncedSearch);
})();
