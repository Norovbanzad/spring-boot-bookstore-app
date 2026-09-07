"use strict";

const BOOK_API = "/api/books";
const CATEGORY_API = "/api/categories";
const AUTHOR_API = "/api/authors";

const form = document.querySelector("#book-form");

const bookIdInput = document.querySelector("#book-id");
const titleInput = document.querySelector("#book-title");
const isbnInput = document.querySelector("#book-isbn");
const priceInput = document.querySelector("#book-price");
const stockInput = document.querySelector("#book-stock");
const categoryInput = document.querySelector("#book-category");
const authorInput = document.querySelector("#book-author");
const activeInput = document.querySelector("#book-active");

const tableBody = document.querySelector("#book-table-body");

const saveButton = document.querySelector("#save-button");
const cancelButton = document.querySelector("#delete-button");
const refreshButton = document.querySelector("#refresh-button");

const message = document.querySelector("#message");

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

console.log(csrfHeader);
console.log(csrfToken);

async function loadCategories() {
  const response = await fetch(CATEGORY_API);

  if (!response.ok) {
    throw new Error("Categories could not be loaded");
  }

  const categories = await response.json();
  categoryInput.innerHTML = `
                <option value="">
                    Select Category
                </option>
                `;
  for (let category of categories) {
    const option = document.createElement("option");
    option.value = category.id;
    option.textContent = category.name;
    categoryInput.appendChild(option);
  }
}

async function loadAuthors() {
  const response = await fetch(AUTHOR_API);

  if (!response.ok) {
    throw new Error("Authors could not be loaded");
  }

  const authors = await response.json();
  authorInput.innerHTML = `
                <option value="">
                    Select Author
                </option>
                `;
  for (let author of authors) {
    const option = document.createElement("option");
    option.value = author.id;
    option.textContent = `${author.firstName} ${author.lastName}`;
    authorInput.appendChild(option);
  }
}

async function loadBooks() {
  try {
    const response = await fetch(BOOK_API);
    const booksData = await response.json();

    if (!response.ok) {
      throw new Error("Could not load the books");
    }

    renderBooks(booksData);
  } catch (error) {
    console.log(error.message);
  }
}

function renderBooks(books) {
  if (books.length === 0) {
    const newRow = document.createElement("tr");
    const messageField = document.createElement("td");
    messageField.textContent = "No books";
    newRow.appendChild(messageField);
    tableBody.appendChild(newRow);
  }

  tableBody.innerHTML = "";

  for (let book of books) {
    const newRow = document.createElement("tr");
    const idField = document.createElement("td");
    idField.textContent = book.id;

    const titleField = document.createElement("td");
    titleField.textContent = book.title;

    const isbnField = document.createElement("td");
    isbnField.textContent = book.isbn;

    const priceField = document.createElement("td");
    priceField.textContent = book.price;

    const stockField = document.createElement("td");
    stockField.textContent = book.stockQuantity;

    const categoryField = document.createElement("td");
    categoryField.textContent = book.categoryName;

    const authorField = document.createElement("td");
    authorField.textContent = `${book.authorFirstName} ${book.authorLastName}`;

    const activeField = document.createElement("td");
    activeField.textContent = book.active;

    newRow.appendChild(idField);
    newRow.appendChild(titleField);
    newRow.appendChild(isbnField);
    newRow.appendChild(priceField);
    newRow.appendChild(stockField);
    newRow.appendChild(categoryField);
    newRow.appendChild(authorField);
    newRow.appendChild(activeField);

    const actionFeild = document.createElement("td");
    actionFeild.className = 'action-buttons';

    const editButton = document.createElement("button");
    editButton.textContent = "Edit";
    editButton.className = "button";
    editButton.classList.add("edit-button");
    editButton.dataset.id = book.id;

    const deleteButton = document.createElement("button");
    deleteButton.textContent = "Delete";
    deleteButton.className = "delete-button";
    deleteButton.classList.add("button");
    deleteButton.dataset.id = book.id;

    actionFeild.appendChild(editButton);
    actionFeild.appendChild(deleteButton);
    newRow.appendChild(actionFeild);
    editButton.addEventListener("click", () => {
		handleEdit(book);
	});
    deleteButton.addEventListener('click', () => {
		deleteBook(book.id)
	});

    tableBody.appendChild(newRow);
  }
  resetForm();
}

form.addEventListener('submit', handleSubmit);

async function handleSubmit(e) {
	e.preventDefault();
	
	const id = bookIdInput.value;
	const book = {
		isbn : isbnInput.value.trim(),
		title : titleInput.value.trim(),
		price : priceInput.value,
		stockQuantity : stockInput.value.trim(),
		active : activeInput ? Boolean(activeInput.checked) : false,
		categoryId : categoryInput.value,
		authorId : authorInput.value	
	};
	
	if(book.title === "") {
		showMessage("Title required ");
		return;
	}
	
	const isEditing = id !== "";
	const url = isEditing ? `${BOOK_API}/${id}` : BOOK_API;
	
	const method = isEditing ? 'PUT' : 'POST';
	
	try {
		const response = await fetch(url, {
			method : method,
			headers : {
				"Content-Type": "application/json",
				[csrfHeader] : csrfToken
			},
			body : JSON.stringify(book)
		});
		
		if(!response.ok) {
			throw new Error('Request Failed');
		}
		
		if(isEditing) {
			showMessage("Book updated");
		}
		else {
			showMessage("Book created");
		}
		
		resetForm();
		
		await loadBooks();
	} catch (error) {
		console.error(error);
		showMessage("Request failed");
	}


}


async function deleteBook(id) {
	const confirmed = confirm("Are you sure to delete this book?");
	if(!confirmed) {
		return;
	}
	
	try {
		const response = await fetch(`${BOOK_API}/${id}`, {
			method :"DELETE",
			headers : {
				[csrfHeader] : csrfToken
			}
		});
		
		if(!response.ok) {
			throw new Error("Delete failed");
		}
		
		showMessage("Book deleted");
		
		resetForm();
		
		await loadBooks();
	} catch(error) {
		console.error(error);
		showMessage("Book could not be deleted");
	}

}

function handleEdit(book) {
    bookIdInput.value = book.id;
    isbnInput.value = book.isbn;
    titleInput.value = book.title
    priceInput.value = book.price;
    stockInput.value = book.stockQuantity;
    categoryInput.value = book.categoryId;
    authorInput.value = book.authorId;
    activeInput.checked = Boolean(book.active);

    saveButton.textContent = "Update book";
}


function resetForm() {
	form.reset();
	bookIdInput.value = "";
    activeInput.checked = true;
	saveButton.textContent = "Save book";
}

function showMessage(text) {
    message.textContent = text;
    message.hidden = false;
    setTimeout(() => {
        message.hidden = true;
    }, 3000)
}

loadCategories();
loadAuthors();
loadBooks();
