"use strict";

const API_URL = "/api/categories";

const form = document.getElementById('category-form');
const categoryIdInput = document.getElementById('category-id');
const nameInput = document.getElementById('category-name');
const descriptionInput = document.getElementById('category-description');
const tableBody = document.getElementById('category-table-body');

//buttons
const cancelButton = document.getElementById('cancel-button');
const submitButton = document.getElementById('submit-button');

console.log("Category JavaScript loaded");

async function loadCategories() {
	try {
		const categoriesResponse = await fetch(API_URL);
		const categoriesData = await categoriesResponse.json();
		console.log(categoriesData);
		
		renderCategories(categoriesData);
		
	} catch (error) {
		console.log(error.message);
	}
}

function renderCategories(categories) {
	
	if(categories.length === 0) {
		tableBody.innerHTML = `<p>No categories found.</p>`;
	}
	
	tableBody.innerHTML = "";
	
	for (let category of categories) {
		const newRow = document.createElement('tr');
		
		const idFeild = document.createElement('td');
		idFeild.textContent = category.id;
		
		const nameField = document.createElement('td');
		nameField.textContent = category.name;
		
		const actionFeild = document.createElement('td');
		actionFeild.className = 'actions';
		
		const editButton = document.createElement('button');
		editButton.textContent = 'Edit';
		editButton.className = 'button';
		editButton.classList.add('edit-button');
		editButton.dataset.id = category.id;

		editButton.addEventListener('click', () => {
			categoryIdInput.value = category.id;
			nameInput.value = category.name;
		});

		actionFeild.appendChild(editButton);
		
		const deleteButton = document.createElement('button');
		deleteButton.textContent = 'Delete';
		deleteButton.className = 'delete-button';
		deleteButton.classList.add('button');
		deleteButton.dataset.id = category.id;
		actionFeild.appendChild(deleteButton);
		
		deleteButton.addEventListener("click", handleDelete);
		
		newRow.appendChild(idFeild);
		newRow.appendChild(nameField);
		newRow.appendChild(actionFeild);
		tableBody.appendChild(newRow);
	}	
	
}

form.addEventListener('submit', handleSubmit);

async function handleSubmit(event) {
	
	event.preventDefault();
	
	const id = categoryIdInput.value.trim();
	const newCategoryName = nameInput.value;
	
	const category = {
		name : newCategoryName
	}

	const isEditing = id !== "";
	const url = isEditing ? `${API_URL}/${id}` : API_URL;

	const method = isEditing ? 'PUT': 'POST';
	
	try {
	
	await fetch(url, {
		method: method,
		
		headers: {
			"Content-Type": "application/json"
		},
		
		body: JSON.stringify(category),
	});

	form.reset();
	categoryIdInput.value = "";
	nameInput.value = "";
	descriptionInput.value = "";
	
	await loadCategories();	
	
	} catch (error) {
		console.log(error);
	}
}

async function handleDelete(event) {
	
	const confirmed = confirm("Delete this category?");
	if(!confirmed) {
		return;
	}
	
	const button = event.target;
	const id = button.dataset.id;
		try {
			await fetch(`${API_URL}/${id}`, {
				method: "DELETE",
			});
			
			const row = button.closest("tr");
			if(row) {
				row.remove();
			}
		} catch (error) {
			console.log(error);
		}
}

cancelButton.addEventListener("click", () => {
	nameInput.value = "";
	descriptionInput.value = "";
});

loadCategories();