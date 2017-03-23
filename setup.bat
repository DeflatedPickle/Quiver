pyinstaller ^
	--console ^
	--onedir ^
	--name="Quiver" ^
	--icon=quiver.ico ^
	--add-data="properties.json;." ^
	--add-data="quiver.ico;." ^
	--add-data="icons/;icons/" ^
	--clean ^
	window.py
