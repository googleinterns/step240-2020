// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
      <a href={props.href}
          target='_blank' rel='noopener noreferrer'>{props.innerText}</a>
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
  const options = props.options;

  return (
    <ul className="navigation">
      {options.map ((option, idx) => 
          <NavigationItem key={idx} href={option.link} innerText={option.text}/>
      )}
    </ul>
  );
}
