import React from 'react';
import Button from 'react-bootstrap/lib/Button';

import Table from 'react-bootstrap/lib/Table';
import Modal from 'react-bootstrap/lib/Modal';
import Form from 'react-bootstrap/lib/Form';

import FormGroup from 'react-bootstrap/lib/FormGroup';
import Col from 'react-bootstrap/lib/Col';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';



class ShowNewToolModal extends React.Component {
    constructor() {
        super();
        this.state = {
            showModal: false,
            name: '',
            description: ''
        }
        this.close = this.close.bind(this);
        this.open = this.open.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    close() {
        this.setState({ showModal: false });
    }

    open() {
        this.setState({ showModal: true });
    }

    handleChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    handleSubmit(event) {
        console.log('A name was submitted: ' + this.state.name);
        console.log('A description was submitted: ' + this.state.description);
        fetch('/localtools', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                name: this.state.name,
                description: this.state.description,
            })
        })
        this.state = {
            showModal: false,
            name: '',
            description: ''
        }
    }

    render() {
        return (
            <div>
                <Button bsStyle="success" onClick={this.open}>
                    New
                </Button>

                <Modal show={this.state.showModal} onHide={this.close}>
                    <Modal.Header closeButton>
                        <Modal.Title>New Tool</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form horizontal onSubmit={this.handleSubmit}>
                            <FormGroup controlId="newToolInputName">
                                <Col componentClass={ControlLabel} sm={2}>
                                    Name
                                </Col>
                                <Col sm={10}>
                                    <input type="text" name="name" value={this.state.name} onChange={this.handleChange} />
                                </Col>
                            </FormGroup>
                            <FormGroup controlId="newToolInputDescription">
                                <Col componentClass={ControlLabel} sm={2}>
                                    Description
                                </Col>
                                <Col sm={10}>
                                    <input type="text" name="description" value={this.state.description} onChange={this.handleChange} />
                                </Col>
                            </FormGroup>
                            <FormGroup>
                                <Col smOffset={2} sm={10}>
                                    <Button type="submit">
                                        Create
                                    </Button>
                                </Col>
                            </FormGroup>
                        </Form>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button onClick={this.close}>Close</Button>
                    </Modal.Footer>
                </Modal>
            </div>
        );
    }
}


class ShowToolList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            tools: ''
        }
    }

    componentDidMount() {
        let client = rest.wrap(mime);
        client({ path: '/localtools',
            headers: {'Accept': 'application/json'}}).then(response => {
            this.setState({tools: response.entity});
        });
    }

    render() {
        const toolList = this.state.tools;
        const arr = toolList instanceof Array ? toolList : [toolList];
        const tools = arr.map((tool) =>
            <Tool tool={tool}/>
        );
        const title = (
            <h3>Tools</h3>
        );


        return (
            <Table responsive>
                <thead >
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                    <th>OpenSource</th>
                    <th>Vendor</th>
                    <th>Creator</th>
                    <th>Date of Creation</th>
                </tr>
                </thead>
                <tbody>
                {tools}
                </tbody>
            </Table>
        );
    }
}