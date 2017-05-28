/*jshint esversion: 6 */

import React from 'react';
import Button from 'react-bootstrap/lib/Button';
import Modal from 'react-bootstrap/lib/Modal';
import ButtonGroup from 'react-bootstrap/lib/ButtonGroup';

export function ShowTool(props) {
    const tool = props.tool;
    return (
        <table>
            <tbody>
                <tr>
                    <th>Name</th>
                    <td>{tool.name}</td>
                </tr>
                <tr>
                    <th>Description</th>
                    <td>{tool.description}</td>
                </tr>
                <tr>
                    <th>Vendor</th>
                    <td>{tool.vendor}</td>
                </tr>
                {tool.creator != null &&
                    <tr>
                        <th>Creator</th>
                        <td>{tool.creator.name}</td>
                    </tr>
                }
                <tr>
                    <th>Opensource?</th>
                    <td>{tool.opensource}</td>
                </tr>
                <tr>
                    <th>Date of Creation</th>
                    <td></td>
                </tr>
            </tbody>
        </table>
    );
}

export class ShowToolModal extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            showModal: false
        }
        this.close = this.close.bind(this);
        this.open = this.open.bind(this);
    }

    close() {
      this.setState({ showModal: false });
    }

    open() {
      this.setState({ showModal: true });
    }

    render() {
      return (
        <div>
            <Button bsStyle="primary" onClick={this.open}>
                show
            </Button>

            <Modal show={this.state.showModal} onHide={this.close}>
                <Modal.Header closeButton>
                    <Modal.Title>Tools</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <ShowTool tool={this.props.tool} />
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={this.close}>Close</Button>
                </Modal.Footer>
          </Modal>
        </div>
      );
    }
}


export function Tool(props) {
    const tool = props.tool;
    return (
        <tr key={tool.name}>
            <th>{tool.name}</th>
            <th>{tool.description}</th>
            <td>{tool.opensource}</td>
            <td>
                <ButtonGroup vertical>
                    <ShowToolModal tool={tool} />
                    <Button bsStyle="danger">Delete</Button>
                </ButtonGroup>
            </td>
        </tr>
    );
}
