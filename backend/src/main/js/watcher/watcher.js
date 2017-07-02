/*jshint esversion: 6 */

import React from 'react';
import Panel from 'react-bootstrap/lib/Panel';
import Table from 'react-bootstrap/lib/Table';

const rest = require('rest');
const mime = require('rest/interceptor/mime');


export class ShowWatcher extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            name: '',
            id: ''
        };
    }

    componentDidMount() {
        let client = rest.wrap(mime);
        client({ path: '/user',
            headers: {'Accept': 'application/json'}}).then(response => {
            this.setState({name: response.entity.name});
            this.setState({id: response.entity.principle});
        });
    }

    render() {
        const title = (
            <h3>Profile</h3>
        );
        return (
            <div>
                <Panel header={title} bsStyle="primary">
                    <Table responsive>
                        <thead >
                        <tr>
                            <th>Name</th>
                            <th>Identifier</th>
                        </tr>
                        </thead>
                        <tbody>
                            <td>{this.state.name}</td>
                            <td>{this.state.id}</td>
                        </tbody>
                    </Table>
                </Panel>
            </div>

        );
    }
}