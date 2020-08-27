import * as React from "react";

/**
 */
const NavigationItem = props => <li><a href={props.href}>{props.innerText}</a></li>

/**
 * Main Navigation List.
 * Instance will be embedded in ./Header component.
 */
export const Navigation = (props) => {
  return (
    <ul>
      {props.options.map(option => <NavigationItem href={option.link} innerText={option.text}/>)}
    </ul>
  );
}
