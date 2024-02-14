<?php
session_start();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $username = $_POST['username'];
    $password = $_POST['password'];

    if ($username === 'admin' && password_verify($password, '$2y$10$2nIO5wBv8TsiMuUNG.GK1.j26zQghRmgn1DYAh/pwrio05USmAJc.')) {
        $_SESSION['authenticated'] = true;
        header('Location: home.php');
        exit();
    } else {
        $error = "Invalid username or password";
    }
}
?>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="style.css">
    <title>Chlorophyllai Gateway - Login</title>
</head>
<body>

<div class="title-bar">
    <h1>Chlorophyllai Gateway</h1>
</div>

<div class="login-container">
    <form action="login.php" method="post">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" required>
        
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required>

        <?php if (isset($error)) { ?>
            <p class="error"><?php echo $error; ?></p>
        <?php } ?>
        
        <button type="submit">Login</button>
    </form>
</div>
