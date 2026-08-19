"use strict";

const API_URL = "/api/categories";

const form = document.getElementById('category-form');
const categoryIdInput = document.getElementById('category-id');
const nameInput = document.getElementById('category-name');
const tableBody = document.getElementById('category-table-body');

//buttons
const cancelButton = document.getElementById('cancel-button');

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
//		
//		newRow.innerHTML = 
//			`<td>${category.id}</td>
//			 <td>${escapeHtml(category.name)}</td>
//			 <td class="actions">
//			    <button class="edit-button button" data-id="${category.id}">Edit</button>
//			 	<button class="delete-button button" data-id="${category.id}">Delete</button>
//			 </td>`;
//		
		tableBody.appendChild(newRow);
	}	
	
}

form.addEventListener('submit', handleSubmit);

async function handleSubmit(event) {
	
	event.preventDefault();
	
	const newCategoryName = nameInput.value;
	
	const category = {
		name : newCategoryName
	}
	
	try {
	
	await fetch(API_URL, {
		method: "POST",
		
		headers: {
			"Content-Type": "application/json"
		},
		
		body: JSON.stringify(category),
	});
	
	
	loadCategories();
	nameInput.value = "";
	
	
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
});


loadCategories();