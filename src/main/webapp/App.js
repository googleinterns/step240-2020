import * as React from "react";

/**
 * Parent Component that is Rendered to the Page.
 * @param {*} props arbituary input properties for our application to use
 */
export const App = (props) => {
	return (
		<h1>Welcome {props.name}</h1>
	)
}