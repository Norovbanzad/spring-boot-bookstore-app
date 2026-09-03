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
const activeInput = document.querySelector("book-active");

const tableBody = document.querySelector("#book-table-body");

const saveButton = document.querySelector("#save-button");
const cancelButton = document.querySelector("#delete-button");
const refreshButton = document.querySelector("#refresh-button");

const message = document.querySelector("#message");

async function loadCategoties() {
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
// editButton.addEventListener("click", handleEdit);
   // deleteButton.addEventListener('click', handleDelete);

    tableBody.appendChild(newRow);
  }
  resetForm();
}

function resetForm() {
	form.reset();
	bookIdInput.value = "";
	titleInput.value = "";
	isbnInput.value = "";
    priceInput.value = "";
    stockInput.value = "";
    categoryInput.value = "";
    authorInput.value = "";
    activeInput.value = false;
	saveButton.textContent = "Save book";
}

function showMessage(text) {
    message.textContent = text;
    message.hidden = false;
    setTimeout(() => {
        message.hidden = true;
    }, 3000)
}

loadCategoties();
loadAuthors();
loadBooks();
