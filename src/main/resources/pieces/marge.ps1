$basePath = Get-Location

$blackDirs = Get-ChildItem -Directory | Where-Object { $_.Name -match '^[A-Z]B$' }
$whiteDirs = Get-ChildItem -Directory | Where-Object { $_.Name -match '^[A-Z]W$' }

$pieces = @()

foreach ($b in $blackDirs) {
    $letter = $b.Name.Substring(0, 1)
    if ($whiteDirs.Name -contains ($letter + "W")) {
        $pieces += $letter
    }
}

foreach ($letter in $pieces) {
    $black = "${letter}B"
    $white = "${letter}W"
    $target = $letter

    Write-Host "marge: $black + $white → $target"

    $targetPath = Join-Path $basePath $target

    if (-not (Test-Path $targetPath)) {
        Copy-Item -Path "$basePath\$black" -Destination $targetPath -Recurse -Force
    }

    $statesPath = Join-Path $targetPath "states"
    $stateDirs = Get-ChildItem -Path $statesPath -Directory

    foreach ($state in $stateDirs) {
        $spritesPath = Join-Path $state.FullName "sprites"

        if (Test-Path $spritesPath) {
            Get-ChildItem -Path $spritesPath -Recurse | Remove-Item -Force -Recurse -ErrorAction SilentlyContinue
        } else {
            New-Item -ItemType Directory -Path $spritesPath | Out-Null
        }

        $sprites0 = Join-Path $spritesPath "sprites0"
        $sprites1 = Join-Path $spritesPath "sprites1"
        New-Item -ItemType Directory -Path $sprites0 -Force | Out-Null
        New-Item -ItemType Directory -Path $sprites1 -Force | Out-Null

        $stateName = $state.Name
        $pbSprites = "$basePath\$black\states\$stateName\sprites"
        $pwSprites = "$basePath\$white\states\$stateName\sprites"

        if (Test-Path $pbSprites) {
            Copy-Item -Path "$pbSprites\*" -Destination $sprites0 -Recurse -Force
        }

        if (Test-Path $pwSprites) {
            Copy-Item -Path "$pwSprites\*" -Destination $sprites1 -Recurse -Force
        }
    }

    Remove-Item -Path "$basePath\$black" -Recurse -Force
    Remove-Item -Path "$basePath\$white" -Recurse -Force
}
