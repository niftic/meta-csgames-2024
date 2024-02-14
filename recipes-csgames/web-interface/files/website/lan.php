<?php
session_start();
if (!isset($_SESSION['authenticated']) || !$_SESSION['authenticated']) {
    header('Location: login.php');
    exit();
}

$arpOutput = shell_exec('arp');
$arpTable = parseArpOutput($arpOutput);

include 'header.php';
?>

<h1>LAN Information</h1>

<div class="content-container">
    <h2>ARP Table</h2>
    <table>
        <thead>
            <tr>
                <th>Interface</th>
                <th>IP Address</th>
                <th>MAC Address</th>
            </tr>
        </thead>
        <tbody>
            <?php foreach ($arpTable as $entry) : ?>
                <tr>
                    <td><?php echo $entry['interface']; ?></td>
                    <td><?php echo $entry['ip']; ?></td>
                    <td><?php echo $entry['mac']; ?></td>
                </tr>
            <?php endforeach; ?>
        </tbody>
    </table>
</div>

<?php
function parseArpOutput($output)
{
    $arpTable = array();
    $lines = explode("\n", $output);

    for ($i = 1; $i < count($lines); $i++) {
        if (preg_match('/(\S+)\s+(\S+)\s+(\S+)/', $lines[$i], $matches)) {
            $entry = array(
                'ip' => $matches[1],
                'interface' => $matches[2],
                'mac' => $matches[3]
            );
            $arpTable[] = $entry;
        }
    }

    return $arpTable;
}
?>
