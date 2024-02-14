<?php
session_start();
if (!isset($_SESSION['authenticated']) || !$_SESSION['authenticated']) {
    header('Location: login.php');
    exit();
}

include 'header.php';
?>

<h1>Diagnostic Tools</h1>

<div class="content-container">
    <h2>Ping and Traceroute</h2>

    <form action="diagnostic.php" method="post">
        <label for="host">Host/IP:</label>
        <input type="text" id="host" name="host" required>

        <label for="type">Select diagnostic tool:</label>
        <select id="type" name="type" required>
            <option value="ping">Ping</option>
            <option value="traceroute">Traceroute</option>
        </select>

        <button type="submit">Submit</button>
    </form>
</div>

<?php
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $host = isset($_POST['host']) ? $_POST['host'] : '';
    $type = isset($_POST['type']) ? $_POST['type'] : '';

    if (empty($host) || empty($type)) {
        echo 'Invalid input';
        exit;
    }

    $address = $_SERVER['SERVER_ADDR'];
    $url = "http://$address/cgi-bin/diagnostic?host=$host&type=$type";
    $result = file_get_contents($url);

    // Display the result
    echo '<div class="diagnostic-result">';
    echo '<h3>Diagnostic Result</h3>';
    echo '<pre>' . htmlspecialchars($result) . '</pre>';
    echo '</div>';
}
?>
