/*jshint esversion: 6 */

import React from 'react';
import Cookies from 'universal-cookie';

import Button from 'react-bootstrap/lib/Button';
import Modal from 'react-bootstrap/lib/Modal';
import Glyphicon from 'react-bootstrap/lib/Glyphicon'
import Form from 'react-bootstrap/lib/Form';
import FormControl from 'react-bootstrap/lib/FormControl'

import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import Table from 'react-bootstrap/lib/Table';
import Panel from 'react-bootstrap/lib/Panel';
import ButtonToolbar from 'react-bootstrap/lib/ButtonToolbar';

import {ShowMovieList} from '../movies/movies'

const rest = require('rest');
const mime = require('rest/interceptor/mime');


export class WatchList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            watchList: props.watchList
        };
    }

    render() {
        const watchList = this.state.watchList;
        return (
            <tr>
                <th>{watchList.name}</th>
                <td><ShowWatchListModal watchList={watchList}/></td>
                <td><ShowWatchListEditModal watchList={watchList} edit="true" /></td>
                <td><DeleteButton watchList={watchList} /></td>
            </tr>
        );
    }
}

class DeleteButton extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            watchList: props.watchList
        };
        this.deleteWatchList = this.deleteWatchList.bind(this);
    }

    deleteWatchList() {
        const id = this.state.watchList.id;
        const cookies = new Cookies();
        const xsrfToken = cookies.get('XSRF-TOKEN');

        let client = rest.wrap(mime);
        client({
            path: '/watchlist/'+id,
            method: 'DELETE',
            headers: {
                'Accept': 'application/json, application/xml, text/plain, text/html, */*',
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': xsrfToken
            },
            credentials: 'same-origin',
            mode: 'cors',
            redirect: 'follow',
        });
    }

    render() {
        return (
            <Button bsStyle="danger" bsSize="small" onClick={this.deleteWatchList}><Glyphicon glyph="trash"/></Button>
        );
    }
}

export class ShowWatchLists extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            watchLists: ''
        };
        this.fetchData = this.fetchData.bind(this);
    }

    componentDidMount() {
        let client = rest.wrap(mime);
        client({ path: '/watchlist',
            headers: {'Accept': 'application/json'}}).then(response => {
            this.setState({watchLists: response.entity});
        });
    }

    fetchData(){
        let client = rest.wrap(mime);
        client({ path: '/watchlist',
            headers: {'Accept': 'application/json'}}).then(response => {
            this.setState({watchLists: response.entity});
        });
    }

    render() {
        const watchLists = this.state.watchLists;
        const arr = watchLists instanceof Array ? watchLists : [watchLists];
        const watchListItems = arr.map((watchList) =>
            <WatchList watchList={watchList} key={watchList.id} />
        );
        const title = (
            <h3>WatchLists</h3>
        );
        const newWatchList = {
            id: 0,
            name: ''
        };

        return (
            <div>
                <Panel header={title} bsStyle="primary">
                    <ButtonToolbar>
                        <ShowWatchListEditModal watchList={newWatchList} />
                        <Button bsStyle="info" bsSize="large" onClick={this.fetchData}><Glyphicon glyph="refresh"/></Button>
                    </ButtonToolbar>

                    <Table responsive>
                        <thead >
                        <tr>
                            <th>Name</th>
                            <th><Glyphicon glyph="zoom-in" /></th>
                            <th><Glyphicon glyph="edit" /></th>
                            <th><Glyphicon glyph="trash" /></th>
                        </tr>
                        </thead>
                        <tbody>
                        {watchListItems}
                        </tbody>
                    </Table>
                </Panel>
            </div>

        );
    }
}


export class ShowWatchListEditModal extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            showModal: false,
            watchList:  props.watchList,
            edit: props.edit,
            showMovies: true
        };
        this.close = this.close.bind(this);
        this.open = this.open.bind(this);
        this.handleChange1 = this.handleChange1.bind(this);
        this.handleSubmit1 = this.handleSubmit1.bind(this);
    }

    close() {
        this.setState({ showModal: false });
    }

    open() {
        this.setState({ showModal: true });
    }

    handleChange1(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const key = target.name;

        const updatedWatchList = this.state.watchList;
        updatedWatchList[key] = value;
        this.setState({
            watchList: updatedWatchList
        });
    }

    handleSubmit1(event) {
        event.preventDefault();

        const cookies = new Cookies();
        const xsrfToken = cookies.get('XSRF-TOKEN');
        // https://hacks.mozilla.org/2016/03/referrer-and-cache-control-apis-for-fetch/
        fetch('/watchlist', {
            method: this.state.edit ? 'POST' : 'PUT',
            headers: {
                'Accept': 'application/json, application/xml, text/plain, text/html, */*',
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': xsrfToken
            },
            credentials: 'same-origin',
            mode: 'cors',
            redirect: 'follow',
            body: JSON.stringify({
                id: this.state.watchList.id,
                name: this.state.watchList.name
            })
        });

        this.setState({ showModal: false });
        this.setState({ watchList: {name: ''} });
    }

    render() {
        return (
            <div>
                <Button bsStyle="success" bsSize={this.state.edit ? 'small' : 'large'} onClick={this.open}>
                    <Glyphicon glyph={this.state.edit ? 'edit' : 'plus'}/>
                </Button>

                <Modal bsSize="large" show={this.state.showModal} onHide={this.close}>
                    <Modal.Header closeButton>
                        <Modal.Title>New WatchList</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form onSubmit={this.handleSubmit1}>
                            <FormGroup controlId="newWatchListInputName">
                                <ControlLabel>Id</ControlLabel>
                                <FormControl type="text" value={this.state.watchList.id} name="name" readOnly />
                                <FormControl.Feedback />
                            </FormGroup>
                            <FormGroup controlId="newWatchListInputName">
                                <ControlLabel>Name</ControlLabel>
                                <FormControl type="text" value={this.state.watchList.name} name="name" placeholder="name" onChange={this.handleChange1} />
                                <FormControl.Feedback />
                            </FormGroup>
                            <Button type="submit">{this.state.edit ? 'Update' : 'Create'}</Button>
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

export class ShowWatchListModal extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            showModal: false
        };
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
                <Button bsStyle="info" bsSize="small" onClick={this.open}>
                    <Glyphicon glyph="zoom-in" />
                </Button>

                <Modal bsSize="large" show={this.state.showModal} onHide={this.close}>
                    <Modal.Header closeButton>
                        <Modal.Title>{this.props.watchList.name}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <ShowWatchList watchList={this.props.watchList} />
                    </Modal.Body>
                    <Modal.Footer>
                        <Button onClick={this.close}>Close</Button>
                    </Modal.Footer>
                </Modal>
            </div>
        );
    }
}

export function ShowWatchList(props) {
    const watchList = props.watchList;
    return (
        <div>
            <ShowMovieList watchListId={watchList.id} />
        </div>
    );
}
