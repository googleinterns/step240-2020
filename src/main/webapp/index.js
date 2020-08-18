import * as React from "react";
import {render} from "react-dom";
import {App} from "./App";

// Render our App Component to element with ID == target
const target = 'root';
render (
	<App name = 'Sam'/>,
	document.getElementById(target)
);