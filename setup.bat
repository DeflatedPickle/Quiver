pyinstaller ^
	--console ^
	--onedir ^
	--name="Quiver" ^
	--icon=quiver.ico ^
	--add-data="properties.json;." ^
	--clean ^
	window.py
