<?php
session_start();
if (!isset($_SESSION['authenticated']) || !$_SESSION['authenticated']) {
    header('Location: login.php');
    exit();
}

include 'header.php';

$netstatOutput = shell_exec('netstat -tuln');

$pattern = '/(tcp|udp).*(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}):(\d+).*/';
preg_match_all($pattern, $netstatOutput, $matches, PREG_SET_ORDER);

echo '<h1>Security Information</h1>';
echo '<div class="content-container">';
echo '<h2>Open Ports</h2>';
echo '<table>';
echo '<tr><th>Protocol</th><th>Listening Address</th><th>Port</th></tr>';

foreach ($matches as $match) {
    $protocol = strtoupper($match[1]);
    $port = $match[2];
    $address = $match[3];

    echo '<tr>';
    echo '<td>' . htmlspecialchars($protocol) . '</td>';
    echo '<td>' . htmlspecialchars($port) . '</td>';
    echo '<td>' . htmlspecialchars($address) . '</td>';
    echo '</tr>';
}

echo '</table>';
echo '</div>';
?>
