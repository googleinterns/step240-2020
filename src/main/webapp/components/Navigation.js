import * as React from 'react';

/**
 * Main List Element to be displayed on the Navigation.
 *
 * @param {Object} props
 * @param {string} props.href hyperlink for the href attribute.
 * @param {string} props.innerText rendered text content of node.
 */
const NavigationItem = (props) => {
  return (
    <li>
      <a href = {props.href}>{props.innerText}</a>
    </li>
  );
}

/**
  * Main Navigation List.
  * Instance will be embedded in ./Header component.
  *
  * @param {Object} props
  * @param {Object} props.options an array of navigation options.
  * @param {string} props.options[].link an external URL attached to option.
  * @param {string} props.options[].text the displayed text for the option.
  */
export const Navigation = (props) => {
  return (
    <ul className="navigation">
      {props.options.map (option, idx => 
          <NavigationItem key={idx} href={option.link} innerText={option.text}/>
      )}
    </ul>
  );
}
