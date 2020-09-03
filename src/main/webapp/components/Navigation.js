import * as React from 'react';

/**
 * Main List Element to be displayed on the Navigation.
 *
 * @param {Object} props
 * @param {String} href hyperlink for the href attribute.
 * @param {String} innerText rendered text content of node.
 */
const NavigationItem = props => {
  <li><a href = {props.href}>{props.innerText}</a></li>
}

/**
  * Main Navigation List.
  * Instance will be embedded in ./Header component.
  *
  * @param {Object} props
  * @param {Object} props.options an array of navigation options.
  * @param {Object} props.options.option a navigation option.
  * @param {String} props.options.option.link an external URL.
  * @param {String} props.options.option.text the displayed text.
  */
export const Navigation = (props) => {
  return (
    <ul>
      {props.options.map (option => 
          <NavigationItem href={option.link} innerText={option.text}/>
      )}
    </ul>
  );
}
