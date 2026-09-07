"use script";

const API_URL = '/api/authors';

const form = document.getElementById('author-form');
const authorIdInput = document.getElementById('author-id');
const firstNameInput = document.getElementById('author-first-name');
const lastNameInput = document.getElementById('author-last-name');
const bioInput = document.getElementById('author-bio');
const tableBody = document.getElementById('author-table-body');
const saveButton = document.getElementById('save-button');
const cancelButton = document.getElementById('cancel-button');
const message = document.getElementById('message');

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

async function loadAuthors() {
    try {
        const response = await fetch(API_URL);
        if(!response.ok) {
            throw new Error("Authors could not be loaded");
        }
        const data = await response.json();

        renderAuthors(data);


    } catch (error) {
        console.error(error);
        showMessage("Authors could not be loaded.");
    }
    
}

function renderAuthors(authors) {
    tableBody.innerHTML = "";
    if(authors.length === 0) {
        const row = document.createElement('tr');
        const cell = document.createElement('td');
        cell.colspan = 5;
        cell.textContent = "No author to display.";

        row.appendChild(cell);
        tableBody.appendChild(row);
        return;
    }

    for (let author of authors) {
        const row = document.createElement('tr');
        const idCell = document.createElement('td');
        idCell.textContent = author.id;
        const firstNameCell = document.createElement('td');
        firstNameCell.textContent = author.firstName;
        const lastNameCell = document.createElement('td');
        lastNameCell.textContent = author.lastName;
        const bioCell = document.createElement('td');
        bioCell.textContent = author.bio ?? ""; // if there is none, return null.

        const actionCell = document.createElement('td');
        const editButton = document.createElement('button');
        editButton.type = 'button';
        editButton.textContent = 'Edit';

        editButton.addEventListener('click', () => {
            startEdit(author);
        });

        const deleteButton = document.createElement('button');
        deleteButton.type = 'button';
        deleteButton.textContent = 'Delete';

        deleteButton.addEventListener('click', () => {
            deleteAuthor(author.id);
        });

        actionCell.appendChild(editButton);
        actionCell.appendChild(deleteButton);
        row.appendChild(idCell);
        row.appendChild(firstNameCell);
        row.appendChild(lastNameCell);
        row.appendChild(bioCell);
        row.appendChild(actionCell);
        tableBody.appendChild(row);
    }

}

form.addEventListener('submit', handleSubmit); // callback hiij, submitiig huleene.

cancelButton.addEventListener('click', resetForm);

async function handleSubmit(e) {
    e.preventDefault();

    const id = authorIdInput.value;
    const author = {
        firstName: firstNameInput.value.trim(),
        lastName : lastNameInput.value.trim(),
        bio: bioInput.value.trim()
    };

    if(author.firstName === "" || author.lastName === "") {
        showMessage("First name and Last name required");
        return;
    }

    const isEditing = id !== "";
    const url = isEditing ? `${API_URL}/${id}` : API_URL;

    const method = isEditing ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers : {
                "Content-Type": "application/json",
				[csrfHeader]: csrfToken
            },
            body : JSON.stringify(author)
        });

        if(!response.ok) {
            throw new Error('Request Failed');
        }

        if (isEditing) {
            showMessage("Author updated successfully");
        } else {
            showMessage("Author created successfully");
        }

        resetForm();

        await loadAuthors();

    } catch (error) {
        console.error(error);
        showMessage("Request failed");
    }

}

function startEdit(author) {
    authorIdInput.value = author.id;
    firstNameInput.value = author.firstName;
    lastNameInput.value = author.lastName;
    bioInput.value = author.bio ?? "";
    saveButton.textContent = 'Update Button';
    cancelButton.hidden = false;
}

 async function deleteAuthor(id) {
    const confirmed = confirm("Are you sure to delete this author?");
    if(!confirmed) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: "DELETE",
			headers: {
				[csrfHeader]: csrfToken
			}
        });
        if(!response.ok) {
            throw new Error('Delete failed');
        }

        showMessage("Author deleted successfully");
        
        resetForm();

        await loadAuthors();
        
    } catch (error) {
        console.error(error);
        showMessage("Author could not be deleted");
    }
}

function resetForm() {
    form.reset();
    authorIdInput.value = "";
    firstNameInput.value = "";
    lastNameInput.value = "";
    bioInput.value = "";
    saveButton.textContent = "Save Button";
    cancelButton.hidden = true;
}

function showMessage(text) {
    message.textContent = text;
    message.hidden = false;
    setTimeout(() => {
        message.hidden = true;
    }, 3000)
}

loadAuthors();