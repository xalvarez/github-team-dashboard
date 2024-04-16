const REFRESH_INTERVAL = 600000

function refreshDashboard () {
  fetch('/dashboard')
    .then(response => response.text())
    .then(responseBody => dashboard.innerHTML = responseBody)
    .then(_ => setLastUpdateTime())

  setTimeout(refreshDashboard, REFRESH_INTERVAL)
}

function setLastUpdateTime () {
  const lastUpdateTimeElement = document.getElementById("#lastupdatetime")
  lastUpdateTimeElement.textContent = new Date().toLocaleTimeString()
}

function filterRows(element) {
  const author = element.value;
  const isChecked = element.checked;
  const isAll = author === 'all';
  const checkboxes = document.querySelectorAll('.form-check-input');
  const rows = document.querySelectorAll('tr[data-author]');
  const allCheckbox = document.querySelector('.form-check-input[value="all"]');
  const noAuthorsSelectedMessage = document.getElementById('no-authors-selected');

  if (isAll) {
    checkboxes.forEach((checkbox) => {
      checkbox.checked = isChecked;
    });
  } else {
    if (!isChecked) {
      allCheckbox.checked = false;
    } else {
      allCheckbox.checked = Array.from(checkboxes).every(
          checkbox => checkbox.checked);
    }
  }

  const anyChecked = Array.from(checkboxes).some(checkbox => checkbox.checked);
  rows.forEach((row) => {
    const rowAuthor = row.getAttribute('data-author');
    const isRowChecked = document.querySelector(`.form-check-input[value="${rowAuthor}"]`)?.checked;
    row.style.display = isRowChecked ? '' : 'none';
  });

  noAuthorsSelectedMessage.style.display = anyChecked ? 'none' : '';
}

refreshDashboard()
