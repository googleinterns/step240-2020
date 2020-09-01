import * as React from 'react';

/**
 * Main List Element to be displayed on the Navigation.
 *
 * @param {href: a URL String to a resource, innerText: The display text}
 */
const NavigationItem = props =>
    <li><a href = {props.href}>{props.innerText}</a></li>
/**
 * Main Navigation List.
 * Instance will be embedded in ./Header component.
 *
 * @param {options: [option]} props arbituary object containing an array of
 * options, where each option = {link:String, text: String}.
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
