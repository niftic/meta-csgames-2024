<?php
session_start();
if (!isset($_SESSION['authenticated']) || !$_SESSION['authenticated']) {
    header('Location: login.php');
    exit();
}
?>

<?php include 'header.php'; ?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="style.css">
    <title>Home</title>
</head>
<body>
    <div class="content-container">
        <h1>Welcome to the Chlorophyllai Gateway Configuration Interface</h1>
        <p>Date: <?php echo date('Y-m-d H:i:s'); ?></p>
        <p>Hostname: <?php echo gethostname(); ?></p>
        <p>IP Address: <?php echo $_SERVER['REMOTE_ADDR']; ?></p>
        <p>Uptime: <?php echo exec('uptime'); ?></p>

        <a href="logout.php">Logout</a>
    </div>
</body>
</html>
