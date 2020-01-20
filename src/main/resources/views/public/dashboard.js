const REFRESH_INTERVAL = 300000

function refreshDashboard() {
  fetch("/dashboard")
    .then(response => response.text())
    .then(responseBody => dashboard.innerHTML = responseBody)
  setTimeout(refreshDashboard, REFRESH_INTERVAL);
}

refreshDashboard()